package com.scooter.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;
    private String type;        // 活动类型：booking, issue, user, system
    private String description; // 活动描述
    private String status;      // 活动状态
    private LocalDateTime time; // 活动时间
} 