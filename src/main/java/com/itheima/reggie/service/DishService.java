package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息以及对应的口味信息
    public DishDto getByIdWithFlavor(Long id);


    //跟新菜品信息，同时更新对应口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜单
    public R<String> delete(List<Long> ids);

    //销售状态
    public R<String> sellStatus(Integer status,List<Long> ids);

    //查询菜单信息
    public R<List<DishDto>> getDishInfo(Dish dish);

}
