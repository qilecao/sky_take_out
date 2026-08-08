package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.DishVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理
 */
@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 获取订单统计信息
     * @return 订单统计VO
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态订单数量统计")
    public Result<OrderStatisticsVO> getStatistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.getStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 完成订单
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("rejection")
    @ApiOperation("拒绝订单")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单订单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> detail(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.getDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 配送订单
     * @param id 订单id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("配送订单")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
}
