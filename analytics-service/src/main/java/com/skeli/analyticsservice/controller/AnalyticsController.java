package com.skeli.analyticsservice.controller;

import com.skeli.analyticsservice.entity.AnalyticsEvent;
import com.skeli.analyticsservice.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsEventRepository analyticsEventRepository;

    @GetMapping("/events")
    public ResponseEntity<List<AnalyticsEvent>> getAllEvents(){
        List<AnalyticsEvent> events = analyticsEventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("events/category/{category}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByCategory(@PathVariable String category){
        List<AnalyticsEvent> events = analyticsEventRepository.findByCategory(category);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getTotalEventsCount(){
        return ResponseEntity.ok(analyticsEventRepository.count());
    }

    @GetMapping("/stats/count/{category}")
    public ResponseEntity<Long> getTotalEventsCountByCategory(@PathVariable String category){
        return ResponseEntity.ok(analyticsEventRepository.countByCategory(category));
    }
}
