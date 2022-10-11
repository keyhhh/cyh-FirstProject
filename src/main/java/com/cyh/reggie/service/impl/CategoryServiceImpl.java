package com.cyh.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.reggie.Entity.Category;
import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.Entity.Setmeal;
import com.cyh.reggie.common.CustomException;
import com.cyh.reggie.mapper.CategoryMapper;
import com.cyh.reggie.service.CategoryService;
import com.cyh.reggie.service.DishService;
import com.cyh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService  {


    @Autowired//菜品service
    private DishService dishService;
    @Autowired//套餐service
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前判断是否关联菜品、套餐，如果关联，抛出业务异常
     * @param id
     */
    @Override
    public void remove(Long id) {


        //查询是否关联菜品
        LambdaQueryWrapper<Dish>  dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类ID进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //已经关联菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        //查询是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类ID进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0) {
            //已经关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }




}
