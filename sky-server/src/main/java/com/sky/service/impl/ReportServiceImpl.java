package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //创建集合用来存放从begin到end的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，每次增加1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //从集合中获取出来日期并转换为字符串，以逗号分隔
        String dateListStr = dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","));


        //创建集合用来存放营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询该日期的营业额数据，状态为已完成的订单金额总和，
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            //查询该日期的营业额数据，状态为已完成的订单金额总和，
            Double turnover = orderMapper.sumByMap(map);
            //如果营业额为null，说明该日期没有已完成的订单，营业额为0
            if (turnover == null) {
                turnover = 0.0;
            }
            //将营业额添加到营业额列表中
            turnoverList.add(turnover);
        }

        //将营业额列表转换为字符串，以逗号分隔
        String turnoverListStr = turnoverList.stream().map(String::valueOf).collect(Collectors.joining(","));

        return TurnoverReportVO
                .builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 用户统计结果
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //创建集合用来存放从begin到end的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，每次增加1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //从集合中获取出来日期并转换为字符串，以逗号分隔
        String dateListStr = dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","));
        //创建集合用来存放用户总量列表
        List<Integer> totalUserList = new ArrayList<>();
        //创建集合用来存放新增用户列表
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询该日期的用户总量数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map1 = new HashMap();
            map1.put("beginTime", beginTime);
            map1.put("endTime", endTime);
            //从用户表中查询该日期的新新增用户总量数据select count(open_id) from user where create_time between beginTime and endTime
            Integer newUser = userMapper.countByMap(map1);
            //将新增用户总量添加到新增用户列表中
            newUserList.add(newUser);

            //从用户表中查询该日期的用户总量数据select count(open_id) from user where create_time <= endTime
            Map map2 = new HashMap();
            map2.put("endTime", endTime);
            Integer totalUser = userMapper.countByMap(map2);
            //将用户总量添加到用户总量列表中
            totalUserList.add(totalUser);
        }
        //将用户总量列表转换为字符串，以逗号分隔
        String totalUserListStr = totalUserList.stream().map(String::valueOf).collect(Collectors.joining(","));
        //将新增用户列表转换为字符串，以逗号分隔
        String newUserListStr = newUserList.stream().map(String::valueOf).collect(Collectors.joining(","));
        UserReportVO userReportVO = UserReportVO
                .builder()
                .dateList(dateListStr)
                .totalUserList(totalUserListStr)
                .newUserList(newUserListStr)
                .build();
        return userReportVO;
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计结果
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //创建集合用来存放从begin到end的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，每次增加1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //从集合中获取出来日期并转换为字符串，以逗号分隔
        String dateListStr = dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","));

        //创建集合用来存放每日订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //创建集合用来存放每日有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();
        //订单总数
        Integer totalOrderCount = 0;
        //有效订单数
        Integer validOrderCount = 0;
        //订单完成率
        Double orderCompletionRate = 0.0;

        for (LocalDate date : dateList) {
            //获取当天时间范围
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //从订单表中查询该日期的订单数select count(*) from orders where create_time between beginTime and endTime

            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer orderCount = orderMapper.countByMap(map);
            //将订单数添加到订单数列表中
            orderCountList.add(orderCount);
            //将订单数添加到订单总数中
            totalOrderCount += orderCount;
            map.put("status", 5);
            Integer validCount = orderMapper.countByMap(map);
            //将有效订单数添加到有效订单数列表中
            validOrderCountList.add(validCount);
            //将有效订单数添加到有效订单总数中
            validOrderCount += validCount;
        }
        //将订单完成率转换为字符串，保留2位小数
        if (totalOrderCount == 0) {
            orderCompletionRate = 0.0;
        } else {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        //将订单数列表转换为字符串，以逗号分隔
        String orderCountListStr = orderCountList.stream().map(String::valueOf).collect(Collectors.joining(","));
        //将有效订单数列表转换为字符串，以逗号分隔
        String validOrderCountListStr = validOrderCountList.stream().map(String::valueOf).collect(Collectors.joining(","));

        OrderReportVO orderReportVO = OrderReportVO
                .builder()
                .dateList(dateListStr)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }

    /**
     * 统计指定时间区间内的菜品销量前十数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 菜品销量前十统计结果
     */
    @Override
    public SalesTop10ReportVO getTop10SalesStatistics(LocalDate begin, LocalDate end) {
        //获取当天时间范围
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //从订单详情表中查询该日期的菜品销量前十数据
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop(beginTime, endTime);
        //获取商品名称列表
        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        //将商品名称列表转换为字符串，以逗号分隔
        String nameListStr = nameList.stream().collect(Collectors.joining(","));

        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        //将商品销量列表转换为字符串，以逗号分隔
        String numberListStr = numberList.stream().map(String::valueOf).collect(Collectors.joining(","));

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO
                .builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
        return salesTop10ReportVO;
    }
}

