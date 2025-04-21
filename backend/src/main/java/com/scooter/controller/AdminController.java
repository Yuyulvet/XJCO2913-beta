package com.scooter.controller;

import com.scooter.dto.StatsDTO;
import com.scooter.dto.ChartDataDTO;
import com.scooter.dto.ActivityDTO;
import com.scooter.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/income/{period}")
    public ResponseEntity<ChartDataDTO> getIncomeData(
            @PathVariable String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getIncomeData(period, startDate, endDate));
    }

    @GetMapping("/duration-distribution")
    public ResponseEntity<List<Integer>> getDurationDistribution() {
        return ResponseEntity.ok(adminService.getDurationDistribution());
    }

    @GetMapping("/activities")
    public ResponseEntity<List<ActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(adminService.getRecentActivities(limit));
    }
} 