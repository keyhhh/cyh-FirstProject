package com.cyh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cyh.reggie.Entity.ShoppingCart;
import com.cyh.reggie.common.BaseContext;
import com.cyh.reggie.common.R;
import com.cyh.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());

        //设置用户id，指定购物车是哪个用户的数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);


        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加的是菜品，根据userID和dishID查询
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加的时套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        //查询当前菜品或者套餐是否早购物车中（数据库购物车表的number字段表示份数）,
        if (cartServiceOne != null) {
            //已经存在，在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //不存在，添加数据，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }


    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if (dishId != null) {
            //减少菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            if (shoppingCart1.getNumber() - 1  == 0)
                shoppingCartService.remove(queryWrapper);
            else{
                updateWrapper.set(shoppingCart1.getNumber() - 1 >= 0, ShoppingCart::getNumber, shoppingCart1.getNumber() - 1);
                shoppingCartService.update(updateWrapper);
            }
        }else {
            //减少套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            if (shoppingCart1.getNumber() - 1  == 0)
                shoppingCartService.remove(queryWrapper);
            else{
                updateWrapper.set(shoppingCart1.getNumber() - 1 >= 0, ShoppingCart::getNumber, shoppingCart1.getNumber() - 1);
                shoppingCartService.update(updateWrapper);
            }

        }
        return R.success("清空成功");
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }


    @DeleteMapping("clean")
    public R<String> clean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
