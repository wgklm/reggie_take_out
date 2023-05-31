package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品
     * @param setmealDto
     */
    public void  saveWithDish(SetmealDto setmealDto);

    /**
     * 更新套餐信息，同时保存套餐和菜品
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 根据id查询套餐信息以及相关联的菜品信息用于回显
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDishes(Long id);

    /**
     * 删除套餐，同时删除套餐和菜品关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 更新停售，起售状态
     * @param status
     * @param ids
     */
    public void updateWithStatus(Integer status,List<Long> ids);
}
