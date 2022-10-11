package com.cyh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyh.reggie.Entity.Category;
import com.cyh.reggie.common.R;
import com.cyh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("添加成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);//分页构造器
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort字段排序
        queryWrapper.orderByAsc(Category::getSort);

        ///进行分页查询
       categoryService.page(pageInfo, queryWrapper);
       return R.success(pageInfo);
    }

    /**
     * 根据id删除分类，
     * @param id 因为id是直接用？拼接在url后边所以直接跟获取
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除的id为:{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }


    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改的id为{}",category.getId());

        categoryService.updateById(category);
        return R.success("更新成功");

    }


    /**
     * 根据条件查询数据<根据前端传来的type进行查询，查出所有所需分类的信息>
     * 再新增菜品时发出的请求，获取全部的分类信息，供添加时选择
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> last(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType() != null ,Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc( Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
