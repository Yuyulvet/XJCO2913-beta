package com.scooter.dto;

import lombok.Data;

@Data
public class StatsDTO {
    private Long totalUsers;        // 总用户数
    private Long activeUsers;       // 活跃用户数
    private Long totalBookings;     // 总预订数
    private Long activeBookings;    // 当前活跃预订数
    private Long totalScooters;     // 总滑板车数
    private Long availableScooters; // 可用滑板车数
    private Long totalIssues;       // 总问题数
    private Long pendingIssues;     // 待处理问题数
    private Double totalIncome;     // 总收入
    private Double monthlyIncome;   // 月收入
} 