package com.skeli.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedEvent {
    private String eventId;
    private String eventName;
    private String description;
    private String venue;
    private String city;
    private String category;
    private LocalDateTime eventDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
    private String organizerId;
    private Long timeStamp;
}
