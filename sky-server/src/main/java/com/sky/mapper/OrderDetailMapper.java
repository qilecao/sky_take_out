package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情数据
     * @param orderDetailList 订单详情列表
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单详情
     * @param orderId 订单id
     * @return 订单详情列表
     */
    List<OrderDetail> getByOrderId(Long orderId);

}
