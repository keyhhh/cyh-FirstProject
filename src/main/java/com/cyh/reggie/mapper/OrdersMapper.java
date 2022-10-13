package com.cyh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyh.reggie.Entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.core.annotation.Order;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
