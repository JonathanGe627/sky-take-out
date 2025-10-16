package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "管理端订单管理接口")
@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult<OrderVO>> conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult<OrderVO> pageResult = orderService.conditionSearchOrder(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @ApiOperation("各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelOrderAdmin(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable("id") Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable("id") Long id){
        orderService.completeOrder(id);
        return Result.success();
    }
}
