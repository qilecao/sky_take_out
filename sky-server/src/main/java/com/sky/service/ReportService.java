package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 统计指定时间区间内的营业额数据
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的用户数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计结果
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的订单数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计结果
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的菜品销量前十数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 菜品销量前十统计结果
     */
    SalesTop10ReportVO getTop10SalesStatistics(LocalDate begin, LocalDate end);

    /**
     * 导出运营数据报表
     * @param response 响应对象
     */
    void exportBusinessData(HttpServletResponse response);

}
