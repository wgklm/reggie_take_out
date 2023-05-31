package com.itheima.reggie.controller;

import com.itheima.reggie.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单详情列表
 */
@RestController
@RequestMapping("/oderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
}
