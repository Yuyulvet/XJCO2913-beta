package com.scooter.service;

import com.scooter.dto.ChartDataDTO;
import java.util.List;

public interface ChartService {
    /**
     * 获取用户增长趋势图表数据
     * @return 用户增长数据
     */
    ChartDataDTO getUserGrowthChart();

    /**
     * 获取收入趋势图表数据
     * @return 收入趋势数据
     */
    ChartDataDTO getIncomeChart();

    /**
     * 获取预订量趋势图表数据
     * @return 预订量趋势数据
     */
    ChartDataDTO getBookingTrendChart();

    /**
     * 获取问题报告趋势图表数据
     * @return 问题报告趋势数据
     */
    ChartDataDTO getIssueReportChart();
} 