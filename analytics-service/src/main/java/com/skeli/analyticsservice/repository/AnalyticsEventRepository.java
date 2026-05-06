package com.skeli.analyticsservice.repository;

import com.skeli.analyticsservice.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    List<AnalyticsEvent> findByCategory(String category);
    List<AnalyticsEvent> findByCity(String city);

    boolean existsByEventId(String eventId);

    long countByCategory(String category);
}
