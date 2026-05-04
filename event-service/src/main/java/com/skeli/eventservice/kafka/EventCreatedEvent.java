package com.skeli.eventservice.kafka;

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
    private String category;
    private LocalDateTime eventDate;
    private String venue;
    private String city;
    private Integer totalSeats;
    private BigDecimal price;
    private String organizerId;
    private Long timeStamp;
}
