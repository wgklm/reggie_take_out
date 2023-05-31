package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    public void submit(Orders orders);

    /**
     * 获取订单详细信息
     * @param orderId
     * @return
     */
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId);

}
