package com.cyh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {


    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish、dishFlavor
    public void  saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);


    //根据id修改菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品数据，同时删除菜品的口味信息
    public void removeWithFlavor(List<Long> ids);
}
