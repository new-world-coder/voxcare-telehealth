package com.voxcare.voice.controller;

import com.voxcare.voice.dto.InitiateVoiceCallRequest;
import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.orchestrator.CommunicationOrchestrator;
import com.voxcare.voice.orchestrator.VoiceRuleConfig;
import com.voxcare.voice.service.VoiceCallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voice")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class VoiceController {

    private final VoiceCallService voiceCallService;
    private final CommunicationOrchestrator orchestrator;

    public VoiceController(VoiceCallService voiceCallService, CommunicationOrchestrator orchestrator) {
        this.voiceCallService = voiceCallService;
        this.orchestrator = orchestrator;
    }

    @PostMapping("/calls")
    public ResponseEntity<VoiceCallResponse> initiateCall(@Valid @RequestBody InitiateVoiceCallRequest request) {
        VoiceRuleConfig rule = orchestrator.resolveActiveRule();
        // If client supplied a custom instruction, temporarily prefer it via a synthetic rule overlay
        if (request.getOutboundInstruction() != null && !request.getOutboundInstruction().isBlank()) {
            rule = new VoiceRuleConfig(
                    rule.id(),
                    rule.name(),
                    rule.enabled(),
                    rule.minQualificationScore(),
                    rule.maxRetries(),
                    rule.retryDelayMinutes(),
                    rule.smsFallbackEnabled(),
                    rule.smsFallbackTemplate(),
                    request.getOutboundInstruction(),
                    rule.priority());
        }
        VoiceCallResponse created = orchestrator.triggerOutboundCall(
                request.getPatientId(),
                request.getTo(),
                request.getProviderId(),
                request.getAppointmentId(),
                request.getPurpose(),
                rule,
                0,
                null);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/calls/{id}")
    public VoiceCallResponse getById(@PathVariable Long id) {
        return voiceCallService.getById(id);
    }

    @GetMapping("/calls/external/{externalId}")
    public VoiceCallResponse getByExternalId(@PathVariable String externalId) {
        return voiceCallService.getByExternalId(externalId);
    }

    @GetMapping("/calls/patient/{patientId}")
    public List<VoiceCallResponse> listByPatient(@PathVariable Long patientId) {
        return voiceCallService.listByPatient(patientId);
    }

    @PostMapping("/calls/{id}/retry")
    public VoiceCallResponse retryCall(@PathVariable Long id) {
        return voiceCallService.retryCall(id);
    }

    @GetMapping("/providers/health")
    public Map<String, Object> providerHealth() {
        return voiceCallService.providerHealth();
    }

    @PostMapping("/reminders/enqueue")
    public Map<String, Object> enqueueReminders() {
        return voiceCallService.enqueueReminderCalls();
    }

    /** Process due EstateCraft-style scheduled_follow_ups (voice_retry). */
    @PostMapping("/retries/process")
    public Map<String, Object> processRetries() {
        int processed = orchestrator.processDueRetries();
        return Map.of("processed", processed);
    }
}
