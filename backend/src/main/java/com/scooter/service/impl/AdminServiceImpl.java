package com.scooter.service.impl;

import com.scooter.dto.StatsDTO;
import com.scooter.dto.ChartDataDTO;
import com.scooter.dto.ActivityDTO;
import com.scooter.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Override
    public StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        // 模拟数据
        stats.setTotalUsers(100L);
        stats.setActiveUsers(50L);
        stats.setTotalBookings(200L);
        stats.setActiveBookings(20L);
        stats.setTotalScooters(30L);
        stats.setAvailableScooters(25L);
        stats.setTotalIssues(10L);
        stats.setPendingIssues(5L);
        stats.setTotalIncome(5000.0);
        stats.setMonthlyIncome(1000.0);
        return stats;
    }

    @Override
    public ChartDataDTO getIncomeData(String period, LocalDate startDate, LocalDate endDate) {
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setTitle("收入趋势");
        chartData.setType("line");
        
        // 模拟数据
        List<String> labels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月");
        List<Double> values = Arrays.asList(500.0, 800.0, 1200.0, 1500.0, 2000.0, 5000.0);
        
        chartData.setLabels(labels);
        chartData.setValues(values);
        return chartData;
    }

    @Override
    public List<Integer> getDurationDistribution() {
        // 模拟数据：1小时内、1-4小时、4-8小时、8-24小时、1天以上
        return Arrays.asList(10, 20, 30, 25, 15);
    }

    @Override
    public List<ActivityDTO> getRecentActivities(int limit) {
        List<ActivityDTO> activities = new ArrayList<>();
        // 模拟数据
        for (int i = 0; i < limit; i++) {
            ActivityDTO activity = new ActivityDTO();
            activity.setId((long) i);
            activity.setType(i % 2 == 0 ? "booking" : "issue");
            activity.setDescription("活动描述 " + i);
            activity.setStatus(i % 3 == 0 ? "成功" : "处理中");
            activity.setTime(LocalDateTime.now().minusHours(i));
            activities.add(activity);
        }
        return activities;
    }
} 