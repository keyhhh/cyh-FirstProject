package com.cyh.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.reggie.Entity.SetmealDish;
import com.cyh.reggie.Entity.Setmeal;
import com.cyh.reggie.common.CustomException;
import com.cyh.reggie.dto.SetmealDto;
import com.cyh.reggie.mapper.SetmealMapper;
import com.cyh.reggie.service.SetmealDishService;
import com.cyh.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setMealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品关系
     *
     * @param setMealDto
     */
    @Override
    public void saveWithDish(SetmealDto setMealDto) {

        //保存套餐基本信息，insert操作setMeal表
        this.save(setMealDto);
        //保存菜品和套餐关联信息， insert操作setMealDish表
        List<SetmealDish> setmealDishes = setMealDto.getSetmealDishes();//但是根据断点可发现，其中缺少表所需的setmealId，遍历导入即可
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setMealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getByIdWithMealDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;

    }


    /**
     * 修改套餐信息和套餐菜品信息
     *
     * @param setmealDto .
     */
    @Override
    public void updateWithMealDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        //删除原来的套餐中菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setMealDishService.remove(queryWrapper);

        //新增修改后的菜品信息
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐信息和套餐的菜品信息
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long>  ids) {
        //查询套餐信息，是否可以删除，status状态码
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        //如果不能删除，抛出业务异常，就是查看id中是否有不满足条件的
        if (count > 0)
            throw new CustomException("存在在售的套餐");

        else{
            //可以删除，先删除套餐表中的数据
            /**
             * 当使用数组接收时使用
             *             LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
             *             setmealLambdaQueryWrapper1.in(Setmeal::getId, ids);
             *             this.remove(setmealLambdaQueryWrapper1);
             */
            this.removeByIds(ids);
            // 删除与套餐相关联的套餐餐品信息

            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
            setMealDishService.remove(setmealDishLambdaQueryWrapper);
        }


    }
}
