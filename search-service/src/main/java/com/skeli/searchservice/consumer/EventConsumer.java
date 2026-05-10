package com.skeli.searchservice.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skeli.searchservice.entity.EventDocument;
import com.skeli.searchservice.repository.EventSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventConsumer {

    private final EventSearchRepository eventSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "event-created", groupId = "search-group")
    public void indexEvent(String message) {
        log.info("Indexing event to Elasticsearch: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String eventId = jsonNode.get("eventId").asText();
            String eventName = jsonNode.has("eventName") ? jsonNode.get("eventName").asText() : "Unknown";
            String description = jsonNode.has("description") ? jsonNode.get("description").asText() : "";
            String venue = jsonNode.has("venue") ? jsonNode.get("venue").asText() : "";
            String city = jsonNode.has("city") ? jsonNode.get("city").asText() : "";
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "";
            String organizerId = jsonNode.has("organizerId") ? jsonNode.get("organizerId").asText() : "";

            int totalSeats = jsonNode.has("totalSeats") ? jsonNode.get("totalSeats").asInt() : 0;
            int availableSeats = jsonNode.has("availableSeats") ? jsonNode.get("availableSeats").asInt() : totalSeats;

            BigDecimal price = jsonNode.has("price") ? jsonNode.get("price").decimalValue() : BigDecimal.ZERO;

            // Handle date array format [year, month, day, hour, minute]
            String eventDate = null;
            if (jsonNode.has("eventDate")) {
                JsonNode dateNode = jsonNode.get("eventDate");
                if (dateNode.isArray() && dateNode.size() >= 5) {
                    // Format as yyyy-MM-dd HH:mm:ss
                    eventDate = String.format("%04d-%02d-%02d %02d:%02d:00",
                            dateNode.get(0).asInt(),
                            dateNode.get(1).asInt(),
                            dateNode.get(2).asInt(),
                            dateNode.get(3).asInt(),
                            dateNode.get(4).asInt()
                    );
                } else if (dateNode.isTextual()) {
                    eventDate = dateNode.asText();
                }
            }

            EventDocument event = EventDocument.builder()
                    .id(eventId)
                    .name(eventName)
                    .description(description)
                    .venue(venue)
                    .city(city)
                    .category(category)
                    .eventDate(eventDate)
                    .totalSeats(totalSeats)
                    .availableSeats(availableSeats)
                    .price(price)
                    .organizerId(organizerId)
                    .build();

            eventSearchRepository.save(event);
            log.info("Event indexed successfully to Elasticsearch: {}", eventId);

        } catch (Exception e) {
            log.error("Failed to index event: {}", e.getMessage(), e);
        }
    }
}