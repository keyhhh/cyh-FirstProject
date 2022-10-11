package com.cyh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyh.reggie.Entity.Category;
import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.Entity.Setmeal;
import com.cyh.reggie.common.R;
import com.cyh.reggie.dto.SetmealDto;
import com.cyh.reggie.service.CategoryService;
import com.cyh.reggie.service.SetmealDishService;
import com.cyh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetmealDishService setMealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息{}", setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除的套餐id为{}", ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 修改套餐信息
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithMealDish(setmealDto);
        return R.success("修改套餐成功");
    }


    /**
     * 分页查询全部套餐
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);


        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);


    }

    @GetMapping("{id}")
    public R<SetmealDto> getByID(@PathVariable Long id) {

        SetmealDto setmealDto = setmealService.getByIdWithMealDish(id);

        log.info("套餐分类名称为{}", setmealDto.getCategoryName());
        return R.success(setmealDto);
    }

    /**
     * 根据id修改t的状态
     *
     * @param status .
     * @param ids    .
     * @return .
     */
    @PostMapping("/status/{status}")
    public R<String> changeSalStatus(@PathVariable int status, Long[] ids) {

        log.info("需要修改的套餐id为{}", Arrays.toString(ids));

        LambdaUpdateWrapper<Setmeal> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        if (ids.length < 2) {
            dishLambdaUpdateWrapper.eq(Setmeal::getId, ids[0]).set(Setmeal::getStatus, status);
        } else {
            for (Long aLong : ids)
                //UPDATE dish SET status=0 WHERE (id = 1397849739276890114 or id = 1397850140982161409 )
                //注意sql语句之间使用or连接，不可以使用AND
                dishLambdaUpdateWrapper.eq(Setmeal::getId, aLong).or();
            dishLambdaUpdateWrapper.set(Setmeal::getStatus, status);
        }
        setmealService.update(dishLambdaUpdateWrapper);
        return R.success("更改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal){
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status != null, Setmeal::getStatus,status);
        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

}
