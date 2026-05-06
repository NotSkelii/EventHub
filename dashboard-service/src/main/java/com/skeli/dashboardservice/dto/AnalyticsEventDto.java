package com.skeli.dashboardservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalyticsEventDto {
    private String eventId;
    private String eventName;
    private String category;
    private String city;
    private LocalDateTime receivedAt;
}
