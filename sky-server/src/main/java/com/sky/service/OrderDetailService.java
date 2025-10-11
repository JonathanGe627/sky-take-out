package com.sky.service;

import com.sky.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    /**
     * 新增OrderDetail
     * @param orderDetailList
     */
    void insertOrderDetailList(List<OrderDetail> orderDetailList);
}
