package com.scooter.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChartDataDTO {
    private String title;           // 图表标题
    private String type;            // 图表类型（line, bar等）
    private List<String> labels;    // X轴标签
    private List<Double> values;    // Y轴数据值

    public ChartDataDTO() {
    }

    public ChartDataDTO(String title, String type, List<String> labels, List<Double> values) {
        this.title = title;
        this.type = type;
        this.labels = labels;
        this.values = values;
    }
} 