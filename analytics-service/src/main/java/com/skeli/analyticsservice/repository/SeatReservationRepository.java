package com.skeli.analyticsservice.repository;

import com.skeli.analyticsservice.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    List<SeatReservation> findByEventId(String eventId);

    List<SeatReservation> findByUserId(String userId);

    List<SeatReservation> findByReservedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByEventId(String eventId);

}
