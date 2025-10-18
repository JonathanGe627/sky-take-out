package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderDetailService;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 1.根据addressBookId查询addressBook
        AddressBook addressBook = addressBookService.getAddressBookById(ordersSubmitDTO.getAddressBookId());
        // 2.封装orders对象
        String number = UUID.randomUUID().toString(true) + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        Orders orders = Orders.builder()
                .number(number)
                .status(Orders.PENDING_PAYMENT)
                .userId(BaseContext.getCurrentId())
                .addressBookId(ordersSubmitDTO.getAddressBookId())
                .orderTime(LocalDateTime.now())
                .payMethod(ordersSubmitDTO.getPayMethod())
                .payStatus(Orders.UN_PAID)
                .amount(ordersSubmitDTO.getAmount())
                .remark(ordersSubmitDTO.getRemark())
                .phone(addressBook.getPhone())
                .address(address)
                .consignee(addressBook.getConsignee())
                .estimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime())
                .deliveryStatus(ordersSubmitDTO.getDeliveryStatus())
                .packAmount(ordersSubmitDTO.getPackAmount())
                .tablewareNumber(ordersSubmitDTO.getTablewareNumber())
                .tablewareStatus(ordersSubmitDTO.getTablewareStatus()).build();
        // 3.新增orders
        orderMapper.insertOrder(orders);
        // 4.查询当前用户购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList();
        // 5.封装orderDetailList
        List<OrderDetail> orderDetailList = shoppingCartList
                .stream().map(shoppingCart -> {
                    OrderDetail orderDetail = BeanUtil.copyProperties(shoppingCart, OrderDetail.class);
                    orderDetail.setOrderId(orders.getId());
                    return orderDetail;
                }).collect(Collectors.toList());
        // 6.新增orderDetailList
        orderDetailService.insertOrderDetailList(orderDetailList);
        // 7.封装返回值
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        // 8.清理购物车数据
        shoppingCartService.cleanShoppingCart();
        // 9.返回数据
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userService.getUserById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
        log.info("跳过微信支付，支付成功");
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        // 推送报警信息到商家
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);
        String jsonStr = JSONUtil.toJsonStr(map);
        webSocketServer.sendToAllClient(jsonStr);
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult<OrderVO> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.page(userId, ordersPageQueryDTO.getStatus());
        List<Orders> ordersList = page.getResult();

        List<Long> orderIdList = ordersList.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderDetail> orderDetailList = orderMapper.getOrderDetailsByOrderIdList(orderIdList);
        Map<Long, List<OrderDetail>> map = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderVO> orderVOList = ordersList.stream().map(order -> {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
            List<OrderDetail> orderDetails = map.get(order.getId());
            orderVO.setOrderDetailList(orderDetails);
            return orderVO;
        }).collect(Collectors.toList());
        PageResult<OrderVO> pageResult = new PageResult<>(page.getTotal(), orderVOList);
        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderVOById(Long id) {
        Long userId = BaseContext.getCurrentId();
        OrderVO orderVO = orderMapper.getOrderVOById(userId, id);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {
        OrderVO orderVO = this.getOrderVOById(id);
        Orders orders = BeanUtil.copyProperties(orderVO, Orders.class);
        if (Orders.PENDING_PAYMENT.equals(orders.getStatus()) || Orders.TO_BE_CONFIRMED.equals(orders.getStatus())){
            // 1.待支付和待接单状态下，用户可直接取消订单
            if (Orders.TO_BE_CONFIRMED.equals(orders.getStatus())){
                // 订单处于待接单状态下取消，需要进行退款
                orders.setPayStatus(Orders.REFUND);
            }
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("用户取消");
            orderMapper.update(orders);
        } else {
            // 2.商家已接单状态下，派送中状态下，用户取消订单需电话沟通商家
            throw new OrderBusinessException(MessageConstant.ORDER_CANT_CANCEL);
        }

    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        OrderVO orderVO = orderMapper.getOrderVOById(userId, id);
        shoppingCartService.repetition(orderVO.getOrderDetailList());
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult<OrderVO> conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.conditionSearchOrder(
                ordersPageQueryDTO.getNumber(),
                ordersPageQueryDTO.getPhone(),
                ordersPageQueryDTO.getStatus(),
                ordersPageQueryDTO.getBeginTime(),
                ordersPageQueryDTO.getEndTime());
        List<Orders> ordersList = page.getResult();
        if (CollUtil.isEmpty(ordersList)){
            return new PageResult<>(0, new ArrayList<>());
        }
        List<Long> orderIdList = ordersList.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderDetail> orderDetailList = orderMapper.getOrderDetailsByOrderIdList(orderIdList);
        Map<Long, List<OrderDetail>> map = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
        List<OrderVO> orderVOList = ordersList.stream().map(order -> {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
            StringBuilder orderDishes = new StringBuilder();
            List<OrderDetail> orderDetails = map.get(order.getId());
            for (int i = 0; i < orderDetailList.size(); i++) {
                orderDishes.append(orderDetailList.get(i).getName()).append("*").append(orderDetailList.get(i).getNumber());
                if (i < orderDetailList.size() - 1) {
                    orderDishes.append(", ");
                }
            }
            orderVO.setOrderDetailList(orderDetails);
            orderVO.setOrderDishes(orderDishes.toString());
            return orderVO;
        }).collect(Collectors.toList());
        PageResult<OrderVO> pageResult = new PageResult<>(page.getTotal(), orderVOList);
        return pageResult;
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        List<Integer> statusList = new ArrayList<>(3);
        statusList.add(Orders.TO_BE_CONFIRMED);
        statusList.add(Orders.CONFIRMED);
        statusList.add(Orders.DELIVERY_IN_PROGRESS);
        List<Orders> ordersList = orderMapper.statistics(statusList);
        Map<Integer, List<Orders>> map = ordersList.stream().collect(Collectors.groupingBy(Orders::getStatus));
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        orderStatisticsVO.setToBeConfirmed(CollUtil.size(map.get(Orders.TO_BE_CONFIRMED)));
        orderStatisticsVO.setConfirmed(CollUtil.size(map.get(Orders.CONFIRMED)));
        orderStatisticsVO.setDeliveryInProgress(CollUtil.size(map.get(Orders.DELIVERY_IN_PROGRESS)));
        return orderStatisticsVO;
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetails(Long id) {
        OrderVO orderVO = orderMapper.getOrderDetails(id);
        List<OrderDetail> orderDetailList = orderVO.getOrderDetailList();
        StringBuilder orderDishes = new StringBuilder();
        for (int i = 0; i < orderDetailList.size(); i++) {
            orderDishes.append(orderDetailList.get(i).getName()).append("*").append(orderDetailList.get(i).getNumber());
            if (i < orderDetailList.size() - 1) {
                orderDishes.append(", ");
            }
        }
        orderVO.setOrderDishes(orderDishes.toString());
        return orderVO;
    }

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Override
    public Orders getOrderById(Long id) {
        return orderMapper.getOrderById(id);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orderDB = this.getOrderById(ordersConfirmDTO.getId());
        if (orderDB == null || !Orders.TO_BE_CONFIRMED.equals(orderDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(order);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orderDB = this.getOrderById(ordersRejectionDTO.getId());
        if (orderDB == null || !Orders.TO_BE_CONFIRMED.equals(orderDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrderAdmin(OrdersCancelDTO ordersCancelDTO) {
        Orders orderDB = this.getOrderById(ordersCancelDTO.getId());
        if (orderDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Integer status = orderDB.getStatus();
        if (Orders.PENDING_PAYMENT.equals(status) || Orders.CONFIRMED.equals(status) || Orders.DELIVERY_IN_PROGRESS.equals(status) || Orders.COMPLETED.equals(status)){
            Orders order = Orders.builder()
                    .id(orderDB.getId())
                    .status(Orders.CANCELLED)
                    .cancelReason(ordersCancelDTO.getCancelReason())
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.update(order);
        }else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orderDB = this.getOrderById(id);
        if (orderDB == null || !Orders.CONFIRMED.equals(orderDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(order);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void completeOrder(Long id) {
        Orders orderDB = this.getOrderById(id);
        if (orderDB == null || !Orders.DELIVERY_IN_PROGRESS.equals(orderDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 修改订单
     * @param order
     */
    @Override
    public void update(Orders order) {
        orderMapper.update(order);
    }

    /**
     * 根据订单状态和下单时间查询订单列表
     * @param status
     * @param time
     * @return
     */
    @Override
    public List<Orders> getOrdersByStatusAndOrderTimeLT(Integer status, LocalDateTime time) {
        return orderMapper.getOrdersByStatusAndOrderTimeLT(status, time);
    }

    /**
     * 用户催单
     * @param id
     */
    @Override
    public void remind(Long id) {
        Orders order = this.getOrderById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("type", 2);
        map.put("orderId", order.getId());
        map.put("content", "订单号：" + order.getNumber());
        String jsonStr = JSONUtil.toJsonStr(map);
        webSocketServer.sendToAllClient(jsonStr);
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @param status
     * @return
     */
    @Override
    public List<OrderStatisticsDTO> statisticsOrder(LocalDate begin, LocalDate end, Integer status) {
        return orderMapper.statisticsOrder(begin, end, status);
    }

    /**
     * 按照日期和状态获取订单数
     * @param dateList
     * @param status
     * @return
     */
    @Override
    public List<Integer> getOrderCountList(List<LocalDate> dateList, Integer status) {
        return orderMapper.getOrderCountList(dateList, status);
    }

    /**
     * 统计一段时间内的订单总数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    @Override
    public Integer getTotalOrderCount(LocalDate begin, LocalDate end, Integer status) {
        return orderMapper.getTotalOrderCount(begin, end, status);
    }

    /**
     * 销量排名Top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public List<SalesTop10StatisticsDTO> top10(LocalDate begin, LocalDate end) {
        return orderMapper.top10(begin, end);
    }

}
