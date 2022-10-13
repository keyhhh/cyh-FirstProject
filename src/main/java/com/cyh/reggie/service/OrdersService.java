package com.cyh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.reggie.Entity.Orders;
import org.springframework.core.annotation.Order;

public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
