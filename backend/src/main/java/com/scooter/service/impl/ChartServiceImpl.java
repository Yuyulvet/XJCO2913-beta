package com.scooter.service.impl;

import com.scooter.dto.ChartDataDTO;
import com.scooter.service.ChartService;
import com.scooter.service.BookingService;
import com.scooter.service.UserService;
import com.scooter.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private IssueService issueService;

    private static final int DAYS_TO_SHOW = 7;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Override
    public ChartDataDTO getUserGrowthChart() {
        List<String> labels = getLastNDaysLabels();
        List<Double> values = new ArrayList<>();
        // 模拟用户增长数据
        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            values.add((double) (10 + i * 2));
        }
        
        return createChartData("用户增长趋势", "line", labels, values);
    }

    @Override
    public ChartDataDTO getIncomeChart() {
        List<String> labels = getLastNDaysLabels();
        List<Double> values = new ArrayList<>();
        // 模拟收入数据
        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            values.add(100.0 + i * 20);
        }
        
        return createChartData("收入趋势", "bar", labels, values);
    }

    @Override
    public ChartDataDTO getBookingTrendChart() {
        List<String> labels = getLastNDaysLabels();
        List<Double> values = new ArrayList<>();
        // 模拟预订量数据
        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            values.add((double) (5 + i * 3));
        }
        
        return createChartData("预订量趋势", "line", labels, values);
    }

    @Override
    public ChartDataDTO getIssueReportChart() {
        List<String> labels = getLastNDaysLabels();
        List<Double> values = new ArrayList<>();
        // 模拟问题报告数据
        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            values.add((double) (2 + i));
        }
        
        return createChartData("问题报告趋势", "bar", labels, values);
    }

    private List<String> getLastNDaysLabels() {
        LocalDate today = LocalDate.now();
        return IntStream.range(0, DAYS_TO_SHOW)
                .mapToObj(i -> today.minusDays(DAYS_TO_SHOW - 1 - i))
                .map(date -> date.format(DATE_FORMATTER))
                .collect(Collectors.toList());
    }

    private ChartDataDTO createChartData(String title, String type, List<String> labels, List<Double> values) {
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setTitle(title);
        chartData.setType(type);
        chartData.setLabels(labels);
        chartData.setValues(values);
        return chartData;
    }
} 