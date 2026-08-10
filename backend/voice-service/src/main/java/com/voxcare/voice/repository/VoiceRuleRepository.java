package com.voxcare.voice.repository;

import com.voxcare.voice.model.VoiceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoiceRuleRepository extends JpaRepository<VoiceRule, Long> {

    List<VoiceRule> findByEnabledTrueOrderByPriorityDesc();

    Optional<VoiceRule> findFirstByEnabledTrueOrderByPriorityDesc();
}
