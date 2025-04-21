package com.scooter.service;

import com.scooter.dto.StatsDTO;
import com.scooter.dto.ChartDataDTO;
import java.util.List;

public interface StatsService {
    /**
     * 获取仪表盘统计数据
     */
    StatsDTO getDashboardStats();
    
    /**
     * 获取用户增长图表数据
     */
    ChartDataDTO getUserGrowthChart();
    
    /**
     * 获取预订量图表数据
     */
    ChartDataDTO getBookingsChart();
    
    /**
     * 获取收入图表数据
     */
    ChartDataDTO getIncomeChart();
    
    /**
     * 获取问题分类图表数据
     */
    ChartDataDTO getIssueTypesChart();
} 