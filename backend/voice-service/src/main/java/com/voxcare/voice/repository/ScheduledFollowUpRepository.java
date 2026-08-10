package com.voxcare.voice.repository;

import com.voxcare.voice.model.ScheduledFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledFollowUpRepository extends JpaRepository<ScheduledFollowUp, Long> {

    List<ScheduledFollowUp> findByProcessedFalseAndScheduledAtLessThanEqualOrderByScheduledAtAsc(LocalDateTime now);
}
