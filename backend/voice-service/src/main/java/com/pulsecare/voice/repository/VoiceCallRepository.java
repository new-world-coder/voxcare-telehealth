package com.pulsecare.voice.repository;

import com.pulsecare.voice.model.VoiceCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoiceCallRepository extends JpaRepository<VoiceCall, Long> {

    Optional<VoiceCall> findByExternalId(String externalId);

    List<VoiceCall> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
