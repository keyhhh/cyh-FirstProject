package com.cyh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.reggie.Entity.Setmeal;

import com.cyh.reggie.dto.SetmealDto;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品关系
     *
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);


    /**
     * 根据id查询套餐信息以及套餐内信息对应的套餐菜品信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithMealDish(Long id);

    /**
     * 修改套餐信息和套餐菜品信息
     * @param setmealDto
     */
    public void updateWithMealDish(SetmealDto setmealDto);


    /**
     * 删除套餐信息和套餐的菜品信息
     * @param ids
     */
    public void removeWithDish( List<Long> ids);
}