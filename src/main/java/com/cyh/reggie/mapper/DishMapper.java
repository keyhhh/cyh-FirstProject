package com.cyh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyh.reggie.Entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
