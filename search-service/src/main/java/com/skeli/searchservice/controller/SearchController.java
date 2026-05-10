package com.skeli.searchservice.controller;


import com.skeli.searchservice.entity.EventDocument;
import com.skeli.searchservice.repository.EventSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {
    private final EventSearchRepository eventSearchRepository;

    @GetMapping("/events")
    public ResponseEntity<List<EventDocument>> searchEvents(@RequestParam String keyword) {
        List<EventDocument> results = eventSearchRepository.findByKeyword(keyword);
        log.info("Search for '{}'. Returned {} results.", keyword, results.size());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/events/category/{category}")
    public ResponseEntity<List<EventDocument>> searchEventsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(eventSearchRepository.findByCategory(category));
    }

    @GetMapping("/events/city/{city}")
    public ResponseEntity<List<EventDocument>> searchEventsByCity(@PathVariable String city) {
        return ResponseEntity.ok(eventSearchRepository.findByCity(city));
    }

    @GetMapping("/events/all")
    public ResponseEntity<Iterable<EventDocument>>searchEvents() {
        return ResponseEntity.ok(eventSearchRepository.findAll());
    }
}
