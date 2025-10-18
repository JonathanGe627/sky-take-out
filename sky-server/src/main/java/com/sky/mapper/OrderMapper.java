package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrderStatisticsDTO;
import com.sky.dto.SalesTop10StatisticsDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据订单状态和下单时间查询订单列表
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getOrdersByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

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

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
