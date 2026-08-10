package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.model.VoiceCall;
import com.voxcare.voice.model.VoiceCallPurpose;
import com.voxcare.voice.orchestrator.CommunicationOrchestrator;
import com.voxcare.voice.orchestrator.VoiceRuleConfig;
import com.voxcare.voice.provider.PhoneNumberInfo;
import com.voxcare.voice.provider.VoiceProvider;
import com.voxcare.voice.repository.VoiceCallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin service facade; Dial outbound orchestration lives in {@link CommunicationOrchestrator}
 * (EstateCraft-compatible).
 */
@Service
public class VoiceCallService {

    private static final Logger log = LoggerFactory.getLogger(VoiceCallService.class);

    private final VoiceProvider voiceProvider;
    private final VoiceCallRepository repository;
    private final VoiceProperties properties;
    private final CommunicationOrchestrator orchestrator;
    private final SchedulingContextClient schedulingContextClient;
    private final OutboundInstructionBuilder instructionBuilder;
    private final PatientLookupClient patientLookupClient;

    public VoiceCallService(
            VoiceProvider voiceProvider,
            VoiceCallRepository repository,
            VoiceProperties properties,
            CommunicationOrchestrator orchestrator,
            SchedulingContextClient schedulingContextClient,
            OutboundInstructionBuilder instructionBuilder,
            PatientLookupClient patientLookupClient) {
        this.voiceProvider = voiceProvider;
        this.repository = repository;
        this.properties = properties;
        this.orchestrator = orchestrator;
        this.schedulingContextClient = schedulingContextClient;
        this.instructionBuilder = instructionBuilder;
        this.patientLookupClient = patientLookupClient;
    }

    @Transactional(readOnly = true)
    public VoiceCallResponse getById(Long id) {
        return repository.findById(id)
                .map(VoiceCallResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice call not found"));
    }

    @Transactional(readOnly = true)
    public VoiceCallResponse getByExternalId(String externalId) {
        return repository.findByExternalId(externalId)
                .map(VoiceCallResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice call not found"));
    }

    @Transactional(readOnly = true)
    public List<VoiceCallResponse> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(VoiceCallResponse::from)
                .toList();
    }

    @Transactional
    public VoiceCallResponse retryCall(Long id) {
        VoiceCall existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice call not found"));
        VoiceRuleConfig rule = orchestrator.resolveActiveRule();
        int retries = existing.getRetryCount() == null ? 0 : existing.getRetryCount();
        if (retries >= rule.maxRetries()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max retries already reached");
        }
        return orchestrator.triggerOutboundCall(
                existing.getPatientId(),
                existing.getToNumber(),
                existing.getProviderId(),
                existing.getAppointmentId(),
                existing.getPurpose(),
                rule,
                retries + 1,
                existing.getId());
    }

    @Transactional
    public Map<String, Object> enqueueReminderCalls() {
        List<ReminderAppointment> appointments = schedulingContextClient.findAppointmentsNeedingReminders();
        List<Long> createdCallIds = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();
        VoiceRuleConfig base = orchestrator.resolveActiveRule();

        for (ReminderAppointment appointment : appointments) {
            if (appointment.patientId() == null) {
                skipped.add(appointment.id());
                continue;
            }
            try {
                PatientInfo patient = patientLookupClient.findById(appointment.patientId());
                List<OpenSlot> slots = schedulingContextClient.findOpenSlots(appointment.providerId());
                String instruction = instructionBuilder.build(
                        VoiceCallPurpose.REMINDER, patient, slots, appointment.appointmentDate());
                VoiceRuleConfig rule = new VoiceRuleConfig(
                        base.id(), base.name(), base.enabled(), base.minQualificationScore(),
                        base.maxRetries(), base.retryDelayMinutes(), base.smsFallbackEnabled(),
                        base.smsFallbackTemplate(), instruction, base.priority());
                VoiceCallResponse created = orchestrator.triggerOutboundCall(
                        appointment.patientId(),
                        null,
                        appointment.providerId(),
                        appointment.id(),
                        VoiceCallPurpose.REMINDER,
                        rule,
                        0,
                        null);
                createdCallIds.add(created.getId());
            } catch (Exception e) {
                log.warn("Failed to enqueue reminder for appointment {}: {}", appointment.id(), e.getMessage());
                skipped.add(appointment.id());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("candidates", appointments.size());
        result.put("enqueued", createdCallIds.size());
        result.put("callIds", createdCallIds);
        result.put("skippedAppointmentIds", skipped);
        return result;
    }

    public Map<String, Object> providerHealth() {
        List<PhoneNumberInfo> numbers;
        try {
            numbers = voiceProvider.listNumbers();
        } catch (Exception e) {
            log.warn("Provider health listNumbers failed: {}", e.getMessage());
            numbers = List.of();
        }
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("provider", voiceProvider.getName());
        health.put("configuredProvider", properties.getProvider());
        health.put("smsFallbackEnabled", properties.isSmsFallbackEnabled());
        health.put("activeVoiceRule", orchestrator.resolveActiveRule().name());
        health.put("dialBaseUrl", properties.getDialBaseUrl());
        health.put("dialFromNumberConfigured",
                properties.getDialFromNumberId() != null && !properties.getDialFromNumberId().isBlank());
        health.put("numbers", numbers);
        return health;
    }
}
