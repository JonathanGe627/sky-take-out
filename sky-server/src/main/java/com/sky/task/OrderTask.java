package com.sky.task;

import cn.hutool.core.collection.CollUtil;
import com.sky.entity.Orders;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * 处理超时未支付订单，将订单状态改为已取消
     */
    @Scheduled(cron = "0 * * * * ?")
    public void cancelOrders(){
        log.info("处理超时未支付订单：" + LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderService.getOrdersByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if(CollUtil.isNotEmpty(ordersList)){
            ordersList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderService.update(order);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void deliveryOrders(){
        log.info("处理派送中订单：" + LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60L);
        List<Orders> ordersList = orderService.getOrdersByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (CollUtil.isNotEmpty(ordersList)){
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(LocalDateTime.now());
                orderService.update(order);
            });
        }
    }
}
