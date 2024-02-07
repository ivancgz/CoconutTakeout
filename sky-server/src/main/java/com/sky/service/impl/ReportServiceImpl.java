package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderMapper orderMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverStatistic(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从 begin 到 end 范围内的每天的日期
        List<LocalDate> dateList = countDate(begin, end);

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询 date 日期对应的营业额数据，营业额是指：状态为 “已完成” 的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            // select sum(amount) from orders where order_time > ? and order_time < ? and status = 5
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        // 封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     * @param begin
     * @param end
     * @return UserReportVO
     */
    @Override
    public UserReportVO getUserStatistic(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从 begin 到 end 范围内的每天的日期
        List<LocalDate> dateList = countDate(begin, end);

        // 存储用户总数和新增用户数的列表
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            // select count(user_id) from orders where order_time > beginTime and order_time < endTime;
            Map map = new HashMap<>();
            map.put("end", endTime);
            Integer userTotal = userMapper.getByDate(map);
            map.put("begin", beginTime);
            Integer newUser = userMapper.getByDate(map);

            totalUserList.add(userTotal);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                           .dateList(StringUtils.join(dateList, ","))
                           .totalUserList(StringUtils.join(totalUserList, ","))
                           .newUserList(StringUtils.join(newUserList, ","))
                           .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return OrderReportVO
     */
    @Override
    public OrderReportVO getOrderStatistic(LocalDate begin, LocalDate end) {
        // 时间区间
        List<LocalDate> dateList = countDate(begin, end);

        // 每日订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        // 每日有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orderCount = orderMapper.countByMap(map);
            map.put("status", Orders.COMPLETED);
            Integer validCount = orderMapper.countByMap(map);

            // 循环里只做一件事
            // totalOrderCount += orderCount;
            // validOrderCount += validCount;

            orderCountList.add(orderCount);
            validOrderCountList.add(validCount);
        }

        // 订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // 有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 订单完成率
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount.doubleValue();

        return OrderReportVO.builder()
                            .dateList(StringUtils.join(dateList, ","))
                            .orderCountList(StringUtils.join(orderCountList, ","))
                            .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                            .totalOrderCount(totalOrderCount)
                            .validOrderCount(validOrderCount)
                            .orderCompletionRate(orderCompletionRate)
                            .build();
    }

    /**
     * 销量排名Top10
     * @param begin
     * @param end
     * @return SalesTop10ReportVO
     */
    @Override
    public SalesTop10ReportVO topTen(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> nameList = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                                 .nameList(StringUtils.join(nameList, ","))
                                 .numberList(StringUtils.join(numberList, ","))
                                 .build();
    }


    private List<LocalDate> countDate(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从 begin 到 end 范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }
}
