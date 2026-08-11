package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

    /**
     * 统计指定时间区间内的营业额数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //创建集合用来存放从begin到end的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            //日期计算，每次增加1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //从集合中获取出来日期并转换为字符串，以逗号分隔
        String dateListStr = dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","));




        //创建集合用来存放营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
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
            if(turnover == null){
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
}
