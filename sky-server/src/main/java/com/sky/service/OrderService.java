package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult<OrderVO> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderVOById(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult<OrderVO> conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetails(Long id);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    Orders getOrderById(Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelOrderAdmin(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void completeOrder(Long id);

    /**
     * 修改订单
     * @param order
     */
    void update(Orders order);

    /**
     * 根据订单状态和下单时间查询订单列表
     * @param status
     * @param time
     * @return
     */
    List<Orders> getOrdersByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    /**
     * 用户催单
     * @param id
     */
    void remind(Long id);

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @param status
     * @return
     */
    List<OrderStatisticsDTO> statisticsOrder(LocalDate begin, LocalDate end, Integer status);

    /**
     * 按照日期和状态获取订单数
     * @param dateList
     * @param status
     * @return
     */
    List<Integer> getOrderCountList(List<LocalDate> dateList, Integer status);

    /**
     * 统计一段时间内的订单总数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    Integer getTotalOrderCount(LocalDate begin, LocalDate end, Integer status);

    /**
     * 销量排名Top10
     * @param begin
     * @param end
     * @return
     */
    List<SalesTop10StatisticsDTO> top10(LocalDate begin, LocalDate end);
}
