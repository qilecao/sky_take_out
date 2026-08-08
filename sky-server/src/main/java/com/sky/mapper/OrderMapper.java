package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders 订单
     */
    void insert(Orders orders);

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
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @Select("select * from orders where id = #{orderId}")
    Orders getById(Long orderId);

    /**
     * 根据订单状态查询订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer selectStatus(Integer status);

    /**
     * 分页条件查询订单
     * @param ordersPageQueryDTO 查询条件
     * @return 订单分页结果
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

}
