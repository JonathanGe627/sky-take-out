package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    void insertOrder(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 历史订单查询
     * @param userId
     * @param status
     * @return
     */
    Page<Orders> page(Long userId, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderVOById(Long userId, Long id);

    /**
     * 订单搜索
     * @param number
     * @param phone
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<Orders> conditionSearchOrder(String number, String phone, Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据订单id列表查询出所有对应的订单明细
     * @param orderIdList
     * @return
     */
    List<OrderDetail> getOrderDetailsByOrderIdList(List<Long> orderIdList);

    /**
     * 各个状态的订单数量统计
     * @param statusList
     * @return
     */
    List<Orders> statistics(List<Integer> statusList);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetails(Long id);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getOrderById(Long id);
}
