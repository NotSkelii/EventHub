package com.skeli.eventservice.kafka;

import com.skeli.eventservice.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
}
