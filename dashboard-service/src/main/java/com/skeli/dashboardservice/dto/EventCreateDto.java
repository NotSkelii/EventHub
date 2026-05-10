package com.skeli.dashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventCreateDto {
    private String name;
    private String description;
    private String venue;
    private String city;
    private String category;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventDate;
    private Integer totalSeats;
    private BigDecimal price;
}
