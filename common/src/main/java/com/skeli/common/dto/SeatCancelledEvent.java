package com.skeli.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatCancelledEvent {
    private String eventId;
    private String eventName;
    private String userId;
    private Integer quantity;
    private Integer remainingSeats;
    private Integer totalSeats;
    private LocalDateTime cancelledAt;
    private Long timeStamp;
}
