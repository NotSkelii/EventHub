package com.skeli.analyticsservice.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skeli.analyticsservice.entity.AnalyticsEvent;
import com.skeli.analyticsservice.repository.AnalyticsEventRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AnalyticsEventRespository analyticsEventRespository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "event-created", groupId = "analytics-group")
    @Transactional
    public void consumeEventCreated(String message) {
        log.info("Received message from Kafka Producer as Raw Json: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventId = jsonNode.get("eventId").asText();

            if (analyticsEventRespository.existsByEventId(eventId)) {
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

            analyticsEventRespository.save(analyticsEvent);
            log.info("Event {} saved to database", eventId);

        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
        }

    }
}
