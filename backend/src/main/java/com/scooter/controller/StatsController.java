package com.scooter.controller;

import com.scooter.dto.StatsDTO;
import com.scooter.dto.ChartDataDTO;
import com.scooter.service.UserService;
import com.scooter.service.BookingService;
import com.scooter.service.ScooterService;
import com.scooter.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ScooterService scooterService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/summary")
    public ResponseEntity<StatsDTO> getStats() {
        StatsDTO stats = new StatsDTO();
        // TODO: 从各个服务获取实际数据
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
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/charts/users")
    public ResponseEntity<ChartDataDTO> getUserGrowth() {
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setTitle("用户增长趋势");
        chartData.setType("line");
        chartData.setLabels(Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月"));
        chartData.setValues(Arrays.asList(10.0, 20.0, 35.0, 45.0, 60.0, 100.0));
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/charts/bookings")
    public ResponseEntity<ChartDataDTO> getBookingStats() {
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setTitle("预订统计");
        chartData.setType("bar");
        chartData.setLabels(Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        chartData.setValues(Arrays.asList(30.0, 25.0, 35.0, 40.0, 50.0, 60.0, 45.0));
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/charts/income")
    public ResponseEntity<ChartDataDTO> getIncomeStats() {
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setTitle("收入统计");
        chartData.setType("line");
        chartData.setLabels(Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月"));
        chartData.setValues(Arrays.asList(500.0, 800.0, 1200.0, 1500.0, 2000.0, 5000.0));
        return ResponseEntity.ok(chartData);
    }
} 