package com.cyh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyh.reggie.Entity.Category;
import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.Entity.DishFlavor;
import com.cyh.reggie.common.R;
import com.cyh.reggie.dto.DishDto;
import com.cyh.reggie.service.CategoryService;
import com.cyh.reggie.service.DishFlavorService;
import com.cyh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增成功");
    }


    /**
     * 删除菜品，要先判断是否在起售状态，只有在停售才可以删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }


    /**
     * 菜品信息分页
     * service.page(),可以有两个个参数，一个page对象，一个查询条件；
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("要查询的菜品名称为{}", name);
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        Page<Dish> page1 = dishService.page(pageInfo, dishLambdaQueryWrapper);

        //对象拷贝,不拷贝records-》protected List<T> records是我们数据对象的集合，需要先处理加入categoryID
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {//每个item对应List<Dish>数据
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //获取分类id
            Category category = categoryService.getById(categoryId); //根据id来查询分类对象
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }


    /**
     * 修改菜品信息页面数据回显
     * 1.首先会发送ajax请求，category/list -》根据菜品id获取菜品分类
     * 2.根据菜品id查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id) {
        log.info("修改的菜品id为{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改的菜品id为{}", dishDto.getId());

        dishService.updateWithFlavor(dishDto);
        return R.success("修改 成功");
    }


    /**
     * 根据id修改餐品的状态
     *
     * @param status .
     * @param id     .
     * @return .
     */
    @PostMapping("/status/{status}")
    public R<String> changeSalStatus(@PathVariable int status, Long[] id) {
//        Dish dish = new Dish();
//        dish.setStatus(status);
//        dish.setId(id);
//        dishService.updateById(dish);
        log.info("需要修改的菜品id为{}", Arrays.toString(id));

        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        if (id.length < 2) {
            dishLambdaUpdateWrapper.eq(Dish::getId, id[0]).set(Dish::getStatus, status);
        } else {
            for (Long aLong : id)
                //UPDATE dish SET status=0 WHERE (id = 1397849739276890114 or id = 1397850140982161409 )
                //注意sql语句之间使用or连接，不可以使用AND
                dishLambdaUpdateWrapper.eq(Dish::getId, aLong).or();
            dishLambdaUpdateWrapper.set(Dish::getStatus, status);
        }
        dishService.update(dishLambdaUpdateWrapper);
        return R.success("更改成功");
    }


//    /**
//     * 前端传的数据是categoryID，可以使用categoryID接收参数，但是为了通用，使用dish对象接受，
//     *
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getStatus, 1).eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId()).orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        return R.success(list);
//    }


    /**
     * 前端传的数据是categoryID，可以使用categoryID接收参数，但是为了通用，使用dish对象接受，
     *
     * 改为dishDto是因为前端显示还需要口味信息
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1).eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId()).orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);


        List<DishDto> dishDtoList = list.stream().map((item) -> {//每个item对应List<Dish>数据
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //获取分类id
            Category category = categoryService.getById(categoryId); //根据id来查询分类对象
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            //当前菜品id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<DishFlavor>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
