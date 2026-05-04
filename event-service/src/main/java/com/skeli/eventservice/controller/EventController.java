package com.skeli.eventservice.controller;

import com.skeli.eventservice.dto.EventRequestDto;
import com.skeli.eventservice.dto.EventResponseDto;
import com.skeli.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto request,
                                                        @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String organizerId) {
        EventResponseDto response = eventService.createEvent(request, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable String id) {
        EventResponseDto response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable String id,
                                                        @Valid @RequestBody EventRequestDto request,
                                                        @RequestHeader(value = "X-User-Id") String organizerId) {
        EventResponseDto response = eventService.updateEvent(id, request, organizerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id, @RequestHeader(value = "X-User-Id") String organizerId) {
        eventService.deleteEvent(id, organizerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveSeats(@PathVariable String id, @RequestParam int quantity,
                                             @RequestHeader(value = "X-User-Id") String userId) {
        eventService.reserveSeats(id, quantity, userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/cancel-reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id, @RequestParam int quantity) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().build();
        }

        eventService.cancelReservation(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> searchEvents(@RequestParam(required = false) String category,
                                                               @RequestParam(required = false) String city,
                                                               @PageableDefault(size = 20, sort = "eventDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EventResponseDto> events = eventService.searchEvents(category, city, pageable);
        return ResponseEntity.ok(events);
    }
}
