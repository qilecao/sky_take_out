package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出运营数据报表
     * @param response 响应对象
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库，获取营业额数据（近三十天）
        //获取近三十天的日期范围
        LocalDate dataBegin = LocalDate.now().minusDays(30);
        LocalDate dataEnd = LocalDate.now().minusDays(1);

        LocalDateTime beginTime = LocalDateTime.of(dataBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dataEnd, LocalTime.MAX);
        //从工作空间服务中获取营业额数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);

        //通过POI将营业额数据写入Excel文件
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        if (input == null) {
            throw new RuntimeException("报表模板文件不存在：template/运营数据报表模板.xlsx");
        }

        //设置响应头，告诉浏览器以附件形式下载Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=运营数据报表.xlsx");

        try (XSSFWorkbook excel = new XSSFWorkbook(input);
             ServletOutputStream outputStream = response.getOutputStream()) {
            //获取表格文件的sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据---时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dataBegin + "至" + dataEnd);

            //获取第四行，填充数据
            XSSFRow row = sheet.getRow(3);

            row.getCell(2).setCellValue(businessDataVO.getTurnover());//营业额
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());//订单完成率
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());//新增用户数

            //获取第五行，填充数据
            row = sheet.getRow(5);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());//有效订单数
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());//平均客单价

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate data = dataBegin.plusDays(i);
                //查询某一天的营业额数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(data, LocalTime.MIN), LocalDateTime.of(data, LocalTime.MAX));
                //填充数据
                row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(data.toString());//日期
                row.getCell(2).setCellValue(businessData.getTurnover());//营业额
                row.getCell(3).setCellValue(businessData.getValidOrderCount());//有效订单数
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());//订单完成率
                row.getCell(5).setCellValue(businessData.getUnitPrice());//平均客单价
                row.getCell(6).setCellValue(businessData.getNewUsers());//新增用户数
            }

            //通过输出流将Excel文件下载到客户端浏览器
            excel.write(outputStream);
        } catch (IOException e) {
            log.error("导出运营数据报表失败", e);
            throw new RuntimeException("导出运营数据报表失败", e);
        }
    }
}

