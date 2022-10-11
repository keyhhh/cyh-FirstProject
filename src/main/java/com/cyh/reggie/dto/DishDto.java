package com.cyh.reggie.dto;

import com.cyh.reggie.Entity.Dish;
import com.cyh.reggie.Entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO Data Transfer Object数据传输对象，用于展示层和服务层之间的数据传输
 */

@Data
public class DishDto extends Dish {

    //继承的Dish的所有属性，又扩展一些属性，添加菜品所需要
    private List<DishFlavor> flavors = new ArrayList<>();

    //分页展示菜品信息需要的属性
    private String categoryName;

    private Integer copies;
}
