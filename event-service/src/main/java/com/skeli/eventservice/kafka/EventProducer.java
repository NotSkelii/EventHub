package com.skeli.eventservice.kafka;

import com.skeli.common.dto.SeatCancelledEvent;
import com.skeli.common.dto.SeatReservedEvent;
import com.skeli.eventservice.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventCreated(Event event){
        EventCreatedEvent eventCreated = EventCreatedEvent.builder()
                .eventId(event.getId())
                .eventName(event.getName())  // Make sure this matches DTO
                .venue(event.getVenue())
                .city(event.getCity())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .totalSeats(event.getTotalSeats())
                .price(event.getPrice())  // BigDecimal
                .organizerId(event.getOrganizerId())
                .timeStamp(System.currentTimeMillis())  // Note: timeStamp with capital S
                .build();

        kafkaTemplate.send("event-created", event.getId(), eventCreated);
        log.info("Event created: {}", event.getId());
    }

    public void sendSeatReserved(Event event, int quantity, String userId){
        SeatReservedEvent seatReserved = SeatReservedEvent.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .userId(userId)
                .quantity(quantity)
                .remainingSeats(event.getAvailableSeats())
                .totalSeats(event.getTotalSeats())
                .reservedAt(LocalDateTime.now())
                .timeStamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send("seat-reserved", event.getId(), seatReserved);
        log.info("{} seats reserved for event: {} by user: {}", quantity, event.getId(), userId);
    }

    public void sendSeatCancelled(Event event, int quantity, String userId){
        SeatCancelledEvent seatCancelled = SeatCancelledEvent.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .userId(userId)
                .quantity(quantity)
                .remainingSeats(event.getAvailableSeats())
                .totalSeats(event.getTotalSeats())
                .cancelledAt(LocalDateTime.now())
                .timeStamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send("seat-cancelled", event.getId(), seatCancelled);
        log.info("{} seats cancelled for event: {} by user: {}", quantity, event.getId(), userId);
    }
}
