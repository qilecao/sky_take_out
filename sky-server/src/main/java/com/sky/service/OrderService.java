package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO 订单提交DTO
     * @return 订单提交VO
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
     * 取消订单(管理端)
     * @param ordersCancelDTO
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 获取订单统计信息
     * @return 订单统计VO
     */
    OrderStatisticsVO getStatistics();

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 订单详情
     * @param id 订单id
     * @return 订单详情
     */
    OrderVO getDetail(Long id);

    /**
     * 配送订单
     * @param id 订单id
     */
    void delivery(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

}
