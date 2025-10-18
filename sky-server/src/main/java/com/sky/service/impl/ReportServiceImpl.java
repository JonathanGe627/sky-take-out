package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.dto.OrderStatisticsDTO;
import com.sky.dto.SalesTop10StatisticsDTO;
import com.sky.entity.Orders;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出运营数据excel报表
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        Double turnover = businessData.getTurnover();
        Integer validOrderCount = businessData.getValidOrderCount();
        Double orderCompletionRate = businessData.getOrderCompletionRate();
        Double unitPrice = businessData.getUnitPrice();
        Integer newUsers = businessData.getNewUsers();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            XSSFRow row3 = sheet.getRow(3);
            row3.getCell(2).setCellValue(turnover);
            row3.getCell(4).setCellValue(validOrderCount);
            row3.getCell(6).setCellValue(orderCompletionRate);
            XSSFRow row4 = sheet.getRow(4);
            row4.getCell(2).setCellValue(unitPrice);
            row4.getCell(4).setCellValue(newUsers);
            XSSFRow row;
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            //关闭资源
            outputStream.flush();
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
