package com.skeli.analyticsservice.repository;

import com.skeli.analyticsservice.entity.SeatCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatCancellationRepository extends JpaRepository<SeatCancellation, Long> {

    List<SeatCancellation> findByEventId(String eventId);

    List<SeatCancellation> findByUserId(String userId);

    List<SeatCancellation> findByCancelledAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
