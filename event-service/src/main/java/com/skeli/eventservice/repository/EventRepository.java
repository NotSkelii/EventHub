package com.skeli.eventservice.repository;

import com.skeli.eventservice.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    Page<Event> findByOrganizerId(String organizer, Pageable pageable);
    Page<Event> findByCategory(String category, Pageable pageable);
    Page<Event> findByCityAndEventDateAfter(String city, LocalDateTime date, Pageable pageable);
    Page<Event> findByEventDateAfter(LocalDateTime date, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") String id);

    Boolean existsByIdAndOrganizerId(String id, String organizerId);
}
