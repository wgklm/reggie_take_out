package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto){
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品Id

        //菜品口味
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品的口味数据到菜品表口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id来擦寻菜品信息以及对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品的基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜单
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前菜品对应口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors=dishDto.getFlavors();

        flavors=flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }


    /**
     * 删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public  R<String> delete(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(dishLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("菜品正在售卖中！无法删除");
        }

        //删除菜品
        dishMapper.deleteBatchIds(ids);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorMapper.delete(dishFlavorLambdaQueryWrapper);
        return R.success("删除菜品成功！");
    }


    /**
     * 更新菜品销售状态
     * @param status
     * @param ids
     * @return
     */
    @Override
    public R<String> sellStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(ids !=null,Dish::getId,ids);

        List<Dish> list=dishMapper.selectList(queryWrapper);

        for(Dish dish : list){
            if (dish !=null){
                dish.setStatus(status);
                dishMapper.updateById(dish);
            }
        }
        return R.success("售卖状态更改成功");
    }

    /**
     * 查询菜品信息
     * @param dish
     * @return
     */

    //第一种只有数据
    /**
    public R<List<Dish>> getDishInfo(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishMapper.selectList(queryWrapper);


        return R.success(list);*/
    @Override
    //第二种
    public R<List<DishDto>> getDishInfo(Dish dish) {
        List<DishDto> dishDtoList =null;
        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis中获取获取缓存数据
        dishDtoList=(List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null){
            //如果存在，直接返回，无需查询数据库
           return R.success(dishDtoList);
        }

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishMapper.selectList(queryWrapper);
        dishDtoList=list.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQl:select * from dish_flavor where dish id =?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
