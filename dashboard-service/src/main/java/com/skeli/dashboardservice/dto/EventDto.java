package com.skeli.dashboardservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventDto {
    private String id;
    private String name;
    private String description;
    private String venue;
    private String city;
    private String category;
    private LocalDateTime eventDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
    private String organizerId;
}
