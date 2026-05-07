package com.skeli.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_cancellation", indexes = {
        @Index(name = "idx_cancel_event_id", columnList = "eventId"),
        @Index(name = "idx_cancel_user_id", columnList = "userId"),
        @Index(name = "idx_cancel_cancelled_at", columnList = "cancelledAt")
})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    private String eventName;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Integer quantity;

    private Integer remainingSeats;

    private Integer totalSeats;

    @Column(nullable = false)
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
