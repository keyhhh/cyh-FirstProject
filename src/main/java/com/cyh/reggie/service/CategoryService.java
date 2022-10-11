package com.cyh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.reggie.Entity.Category;


/**
 * 继承mybatis plus 提供了save方法
 */

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
