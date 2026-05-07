package com.skeli.analyticsservice.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skeli.analyticsservice.entity.AnalyticsEvent;
import com.skeli.analyticsservice.entity.SeatCancellation;
import com.skeli.analyticsservice.entity.SeatReservation;
import com.skeli.analyticsservice.repository.AnalyticsEventRepository;
import com.skeli.analyticsservice.repository.SeatCancellationRepository;
import com.skeli.analyticsservice.repository.SeatReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AnalyticsEventRepository analyticsEventRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final SeatCancellationRepository seatCancellationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "event-created", groupId = "analytics-group")
    @Transactional
    public void consumeEventCreated(String message) {
        log.info("Received message from Kafka Producer as Raw Json: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventId = jsonNode.get("eventId").asText();

            if (analyticsEventRepository.existsByEventId(eventId)) {
                log.warn("Event {} already exists in database, skipping", eventId);
                return;
            }
            String eventName = jsonNode.has("eventName") ? jsonNode.get("eventName").asText() : "Unknown";
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "Unknown";
            String city = jsonNode.has("city") ? jsonNode.get("city").asText() : "Unknown";

            AnalyticsEvent analyticsEvent = AnalyticsEvent.builder()
                    .eventId(eventId)
                    .eventName(eventName)
                    .category(category)
                    .city(city)
                    .rawMessage(message)
                    .build();

            analyticsEventRepository.save(analyticsEvent);
            log.info("Event {} saved to database", eventId);

        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "seat-reserved", groupId = "analytics-group")
    @Transactional
    public void consumeSeatReserved(String message) {
        log.info("Received seat-reserved message from Kafka Producer as Raw Json: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String eventId = jsonNode.get("eventId").asText();
            String eventName = jsonNode.has("eventName") ? jsonNode.get("eventName").asText() : "Unknown";
            String userId = jsonNode.get("userId").asText();
            int quantity = jsonNode.get("quantity").asInt();
            int remainingSeats = jsonNode.get("remainingSeats").asInt();
            int totalSeats = jsonNode.get("totalSeats").asInt();

            LocalDateTime reservedAt = null;
            if (jsonNode.has("reservedAt") && jsonNode.get("reservedAt").isArray()) {
                var arr = jsonNode.get("reservedAt");
                reservedAt = LocalDateTime.of(
                        arr.get(0).asInt(),
                        arr.get(1).asInt(),
                        arr.get(2).asInt(),
                        arr.get(3).asInt(),
                        arr.get(4).asInt(),
                        arr.get(5).asInt()
                );
            } else {
                reservedAt = LocalDateTime.now();
            }

            SeatReservation seatReservation = SeatReservation.builder()
                    .eventId(eventId)
                    .eventName(eventName)
                    .userId(userId)
                    .quantity(quantity)
                    .remainingSeats(remainingSeats)
                    .totalSeats(totalSeats)
                    .reservedAt(reservedAt)
                    .build();

            seatReservationRepository.save(seatReservation);
            log.info("Seat reservation saved: {} seats for event: {} by user: {}", quantity, eventId, userId);
        } catch (Exception e) {
            log.error("Failed to process seat-reservation message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "seat-cancelled", groupId = "analytics-group")
    @Transactional
    public void consumeSeatCancelled(String message) {
        log.info("Received seat-cancelled message from Kafka Producer as Raw Json: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String eventId = jsonNode.get("eventId").asText();
            String eventName = jsonNode.has("eventName") ? jsonNode.get("eventName").asText() : "Unknown";
            String userId = jsonNode.get("userId").asText();
            int quantity = jsonNode.get("quantity").asInt();
            int remainingSeats = jsonNode.get("remainingSeats").asInt();
            int totalSeats = jsonNode.get("totalSeats").asInt();

            LocalDateTime cancelledAt = null;
            if (jsonNode.has("cancelledAt") && jsonNode.get("cancelledAt").isArray()) {
                var arr = jsonNode.get("cancelledAt");
                cancelledAt = LocalDateTime.of(
                        arr.get(0).asInt(),
                        arr.get(1).asInt(),
                        arr.get(2).asInt(),
                        arr.get(3).asInt(),
                        arr.get(4).asInt(),
                        arr.get(5).asInt()
                );
            } else {
                cancelledAt = LocalDateTime.now();
            }

            SeatCancellation seatCancellation = SeatCancellation.builder()
                    .eventId(eventId)
                    .eventName(eventName)
                    .userId(userId)
                    .quantity(quantity)
                    .remainingSeats(remainingSeats)
                    .totalSeats(totalSeats)
                    .cancelledAt(cancelledAt)
                    .build();

            seatCancellationRepository.save(seatCancellation);
            log.info("Seat cancellation saved: {} seats for event: {} by user: {}", quantity, eventId, userId);
        } catch (Exception e) {
            log.error("Failed to process seat-cancellation message: {}", e.getMessage(), e);
        }
    }
}
