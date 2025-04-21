package com.scooter.service;

import com.scooter.dto.StatsDTO;
import com.scooter.dto.ChartDataDTO;
import com.scooter.dto.ActivityDTO;
import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    /**
     * 获取仪表盘统计数据
     */
    StatsDTO getStats();

    /**
     * 获取收入图表数据
     * @param period 时间周期（week/month/year）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     */
    ChartDataDTO getIncomeData(String period, LocalDate startDate, LocalDate endDate);

    /**
     * 获取租赁时长分布数据
     */
    List<Integer> getDurationDistribution();

    /**
     * 获取最近活动记录
     * @param limit 返回记录数量限制
     */
    List<ActivityDTO> getRecentActivities(int limit);
} 