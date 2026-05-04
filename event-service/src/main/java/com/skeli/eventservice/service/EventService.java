package com.skeli.eventservice.service;

import com.skeli.eventservice.dto.EventRequestDto;
import com.skeli.eventservice.dto.EventResponseDto;
import com.skeli.eventservice.entity.Event;
import com.skeli.eventservice.kafka.EventProducer;
import com.skeli.eventservice.mapper.EventMapper;
import com.skeli.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventProducer eventProducer;

    @Transactional
    public EventResponseDto createEvent(EventRequestDto request, String organizerId) {
        log.info("Creating event: {} for organizer: {}", request.getName(), organizerId);

        Event event = eventMapper.toEntity(request);
        event.setOrganizerId(organizerId);

        Event savedEvent = eventRepository.save(event);

        eventProducer.sendEventCreated(savedEvent);

        return eventMapper.toResponseDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        return eventMapper.toResponseDto(event);
    }

    public EventResponseDto updateEvent(String id, EventRequestDto updateRequest, String organizerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("Only the event organizer can update this event.");
        }

        eventMapper.updateEntity(event, updateRequest);
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toResponseDto(updatedEvent);
    }

    @Transactional
    public void deleteEvent(String id, String organizerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("Only the event organizer can delete this event.");
        }
        eventRepository.delete(event);
        log.info("Event deleted: {}", id);
    }

    @Transactional
    public void reserveSeats(String id, int quantity, String userId) {
        log.info("Reserving {} seats for event: {} for user: {}", quantity, id, userId);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        if (event.getAvailableSeats() < quantity) {
            throw new RuntimeException("Not enough seats available for this event. Available seats: " + event.getAvailableSeats());
        }

        event.setAvailableSeats(event.getAvailableSeats() - quantity);
        eventRepository.save(event);
        log.info("Reserved {} seats for event: {} for user {}. Remaining seats: {}", quantity, id, userId, event.getAvailableSeats());
    }

    @Transactional
    public void cancelReservation(String id, int quantity) {
        log.info("Canceling reservation for event: {}, quantity: {}", id, quantity);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        if (quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative.");
        }

        int newAvailableSeats = event.getAvailableSeats() + quantity;
        if (newAvailableSeats > event.getTotalSeats()) {
            throw new RuntimeException("Cannot cancel more seats than were reserved." +
                    "Available seats: " + event.getAvailableSeats() + ", Total seats: " + event.getTotalSeats() +
                    "Attempted to cancel: " + quantity);
        }
        event.setAvailableSeats(newAvailableSeats);
        eventRepository.save(event);
        log.info("Cancelled {} seats for event: {}. Remaining seats: {}", quantity, id, event.getAvailableSeats());
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDto> searchEvents(String category, String city, Pageable pageable) {
        Page<Event> events;
        if (category != null && city != null) {
            events = eventRepository.findByCityAndEventDateAfter(city, java.time.LocalDateTime.now(), pageable);
        } else if (category != null) {
            events = eventRepository.findByCategory(category, pageable);
        } else if (city != null) {
            events = eventRepository.findByCityAndEventDateAfter(city, java.time.LocalDateTime.now(), pageable);
        } else {
            events = eventRepository.findByEventDateAfter(java.time.LocalDateTime.now(), pageable);
        }
        return events.map(eventMapper::toResponseDto);
    }
}
