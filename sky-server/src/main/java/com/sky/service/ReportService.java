package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverStatistic(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的用户数据
     * @param begin
     * @param end
     * @return UserReportVO
     */
    UserReportVO getUserStatistic(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return OrderReportVO
     */
    OrderReportVO getOrderStatistic(LocalDate begin, LocalDate end);

    /**
     * 销量排名Top10
     * @param begin
     * @param end
     * @return SalesTop10ReportVO
     */
    SalesTop10ReportVO topTen(LocalDate begin, LocalDate end);
}
