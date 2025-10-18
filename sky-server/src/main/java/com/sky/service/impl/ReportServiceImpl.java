package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.dto.OrderStatisticsDTO;
import com.sky.dto.SalesTop10StatisticsDTO;
import com.sky.entity.Orders;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        LocalDate endExclusive = end.plusDays(1);
        List<OrderStatisticsDTO> orderStatisticsDTOList = orderService.statisticsOrder(begin, endExclusive, Orders.COMPLETED);
        // 这里使用treeMap，自动按照日期排序（LocalDate 实现了 Comparable）
        Map<LocalDate, BigDecimal> statisticsMap = new TreeMap<>();
        List<LocalDate> dateList = new ArrayList<>();
        List<BigDecimal> turnoverList = new ArrayList<>();
        for (OrderStatisticsDTO orderStatisticsDTO : orderStatisticsDTOList) {
            statisticsMap.put(orderStatisticsDTO.getOrderDate(), orderStatisticsDTO.getTotalAmount());
        }
        LocalDate cursor = begin;
        while (!cursor.equals(endExclusive)) {
            dateList.add(cursor);
            turnoverList.add(statisticsMap.getOrDefault(cursor, BigDecimal.ZERO));
            cursor = cursor.plusDays(1);
        }
        String dateListStr = StrUtil.join(",", dateList);
        String turnoverListStr = StrUtil.join(",", turnoverList);
        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        LocalDate endExclusive = end.plusDays(1);
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate cursor = begin;
        while (!cursor.equals(endExclusive)){
            dateList.add(cursor);
            cursor = cursor.plusDays(1);
        }
        List<Integer> totalUserList = userService.totalUserStatistics(dateList);
        List<String> totalUserStringList = totalUserList.stream().map(String::valueOf).collect(Collectors.toList());
        List<Integer> newUserList = userService.newUserStatistics(dateList);
        List<String> newUserListStringList = newUserList.stream().map(String::valueOf).collect(Collectors.toList());
        return UserReportVO.builder()
                .dateList(StrUtil.join(",", dateList))
                .totalUserList(String.join(",", totalUserStringList))
                .newUserList(String.join(",", newUserListStringList))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        LocalDate endExclusive = end.plusDays(1);
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate cursor = begin;
        while (!cursor.equals(endExclusive)){
            dateList.add(cursor);
            cursor = cursor.plusDays(1);
        }
        List<Integer> orderCountList = orderService.getOrderCountList(dateList, null);
        List<Integer> validOrderCountList = orderService.getOrderCountList(dateList, Orders.COMPLETED);
        Integer totalOrderCount = orderService.getTotalOrderCount(begin, endExclusive, null);
        Integer validOrderCount = orderService.getTotalOrderCount(begin, endExclusive, Orders.COMPLETED);
        Double orderCompletionRate = totalOrderCount.equals(0)? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StrUtil.join(",", dateList))
                .orderCountList(StrUtil.join(",", orderCountList))
                .validOrderCountList(StrUtil.join(",", validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量排名Top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDate endExclusive = end.plusDays(1);
        List<SalesTop10StatisticsDTO> salesTop10StatisticsDTOList = orderService.top10(begin, endExclusive);
        List<String> nameList = salesTop10StatisticsDTOList.stream().map(SalesTop10StatisticsDTO::getSaleName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10StatisticsDTOList.stream().map(SalesTop10StatisticsDTO::getSaleNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StrUtil.join(",", nameList))
                .numberList(StrUtil.join(",", numberList))
                .build();
    }
}
