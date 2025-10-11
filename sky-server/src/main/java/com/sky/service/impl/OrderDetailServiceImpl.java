package com.sky.service.impl;

import com.sky.entity.OrderDetail;
import com.sky.mapper.OrderDetailMapper;
import com.sky.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    OrderDetailMapper orderDetailMapper;

    /**
     * 新增OrderDetail
     * @param orderDetailList
     */
    @Override
    public void insertOrderDetailList(List<OrderDetail> orderDetailList) {
        orderDetailMapper.insertOrderDetailList(orderDetailList);
    }
}
