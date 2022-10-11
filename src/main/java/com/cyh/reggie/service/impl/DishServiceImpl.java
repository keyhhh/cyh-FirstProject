package com.cyh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.Entity.DishFlavor;
import com.cyh.reggie.common.CustomException;
import com.cyh.reggie.dto.DishDto;
import com.cyh.reggie.mapper.DishMapper;
import com.cyh.reggie.service.DishFlavorService;
import com.cyh.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish、dishFlavor
    @Override
    @Transactional//因为是多表操作，所以开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //先保存dish基本信息到菜品表
        this.save(dishDto);

        Long dishDtoId = dishDto.getId();//菜品id

        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味

        ///为每一个口味实体赋予菜品id值，因为前端获取时没有这个值
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());//再次转为list

        //保存菜品口味数据到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /***
     * //根据id查询菜品信息和口味信息
     * @param id .
     * @return .
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id); //查询菜品信息根据id

        BeanUtils.copyProperties(dish, dishDto);//dish ---->dishDto

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();//创建条件构造器
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());//添加过滤条件
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }


    /**
     * //根据id修改菜品信息和口味信息
     * @param dishDto .
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //删除原本的flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);


        //添加修改后的flavor
        List<DishFlavor> dishFlavorsList = dishDto.getFlavors();
        dishFlavorsList = dishFlavorsList.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());//再次转为list


        dishFlavorService.saveBatch(dishFlavorsList);
    }

    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {

        //判断菜品的状态，status为0可以删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);//添加查询条件status状态为1
        int count = this.count(dishLambdaQueryWrapper);
        //如果count》0，就是说存在起售的菜品，抛出业务异常
        if (count > 0)
            throw new CustomException("存在售卖中的餐品");
        else{
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper1.in(Dish::getId, ids);
            this.remove(dishLambdaQueryWrapper1);

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        }

    }
}
