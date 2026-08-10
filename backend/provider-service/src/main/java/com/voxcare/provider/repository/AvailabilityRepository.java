package com.voxcare.provider.repository;

import com.voxcare.provider.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByProviderIdOrderByStartTimeAsc(Long providerId);

    @Query("""
            SELECT a FROM AvailabilitySlot a
            WHERE a.providerId = :providerId
              AND a.status = 'AVAILABLE'
              AND a.startTime >= :from
              AND a.startTime < :to
            ORDER BY a.startTime ASC
            """)
    List<AvailabilitySlot> findOpenSlots(
            @Param("providerId") Long providerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT a FROM AvailabilitySlot a
            WHERE a.status = 'AVAILABLE'
              AND a.startTime >= :from
              AND a.startTime < :to
            ORDER BY a.startTime ASC
            """)
    List<AvailabilitySlot> findAllOpenSlots(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
