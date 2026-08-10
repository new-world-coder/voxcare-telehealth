package com.pulsecare.voice.service;

import com.pulsecare.voice.config.VoiceProperties;
import com.pulsecare.voice.dto.InitiateVoiceCallRequest;
import com.pulsecare.voice.dto.VoiceCallResponse;
import com.pulsecare.voice.model.VoiceCall;
import com.pulsecare.voice.model.VoiceCallPurpose;
import com.pulsecare.voice.provider.CallResult;
import com.pulsecare.voice.provider.CommunicationStatus;
import com.pulsecare.voice.provider.InitiateCallParams;
import com.pulsecare.voice.provider.PhoneNumberInfo;
import com.pulsecare.voice.provider.VoiceProvider;
import com.pulsecare.voice.repository.VoiceCallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class VoiceCallService {

    private static final Logger log = LoggerFactory.getLogger(VoiceCallService.class);

    private final VoiceProvider voiceProvider;
    private final VoiceCallRepository repository;
    private final VoiceProperties properties;
    private final PatientLookupClient patientLookupClient;

    public VoiceCallService(
            VoiceProvider voiceProvider,
            VoiceCallRepository repository,
            VoiceProperties properties,
            PatientLookupClient patientLookupClient) {
        this.voiceProvider = voiceProvider;
        this.repository = repository;
        this.properties = properties;
        this.patientLookupClient = patientLookupClient;
    }

    @Transactional
    public VoiceCallResponse initiateCall(InitiateVoiceCallRequest request) {
        String to = request.getTo();
        if ((to == null || to.isBlank()) && request.getPatientId() != null) {
            to = patientLookupClient.findPhoneByPatientId(request.getPatientId());
        }
        if (to == null || to.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phone number required (pass 'to' or a patientId with phone on file)");
        }

        String instruction = request.getOutboundInstruction();
        if (instruction == null || instruction.isBlank()) {
            instruction = buildDefaultInstruction(request.getPurpose());
        }

        CallResult result = voiceProvider.initiateCall(new InitiateCallParams(
                to,
                null,
                instruction,
                request.getPatientId(),
                request.getPurpose().name()));

        VoiceCall call = new VoiceCall();
        call.setExternalId(result.externalId() != null ? result.externalId() : result.callId());
        call.setProvider(result.provider());
        call.setPurpose(request.getPurpose());
        call.setPatientId(request.getPatientId());
        call.setAppointmentId(request.getAppointmentId());
        call.setProviderId(request.getProviderId());
        call.setToNumber(to);
        call.setStatus(result.status().name());
        call.setOutboundInstruction(instruction);
        call.setRetryCount(0);

        VoiceCall saved = repository.save(call);
        log.info("Voice call persisted id={} externalId={} provider={}",
                saved.getId(), saved.getExternalId(), saved.getProvider());
        return VoiceCallResponse.from(saved);
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
    public void handleDialWebhook(Map<String, Object> event) {
        Object externalIdObj = firstNonNull(
                event.get("call_id"),
                event.get("id"),
                nested(event, "data", "id"));
        if (externalIdObj == null) {
            log.warn("Dial webhook missing call id");
            return;
        }
        String externalId = String.valueOf(externalIdObj);

        VoiceCall call = repository.findByExternalId(externalId).orElse(null);
        if (call == null) {
            log.warn("Dial webhook for unknown externalId={}", externalId);
            return;
        }

        Object statusObj = firstNonNull(event.get("status"), nested(event, "data", "status"));
        if (statusObj != null) {
            call.setStatus(normalizeStatus(String.valueOf(statusObj)));
        }

        Object duration = firstNonNull(event.get("duration"), nested(event, "data", "duration"));
        if (duration instanceof Number n) {
            call.setDurationSeconds(n.intValue());
        }

        Object transcript = firstNonNull(event.get("transcript"), nested(event, "data", "transcript"));
        if (transcript != null) {
            call.setTranscript(String.valueOf(transcript));
        }

        String status = call.getStatus() == null ? "" : call.getStatus().toUpperCase(Locale.ROOT);
        if (status.equals(CommunicationStatus.COMPLETED.name())) {
            call.setOutcome("CONNECTED");
            call.setEndedAt(LocalDateTime.now());
        } else if (status.equals(CommunicationStatus.FAILED.name())
                || status.equals(CommunicationStatus.NO_ANSWER.name())
                || status.equals(CommunicationStatus.BUSY.name())
                || status.equals(CommunicationStatus.CANCELLED.name())) {
            call.setOutcome(status);
            call.setEndedAt(LocalDateTime.now());
        }

        repository.save(call);
        log.info("Dial webhook applied externalId={} status={}", externalId, call.getStatus());
    }

    public Map<String, Object> providerHealth() {
        List<PhoneNumberInfo> numbers;
        try {
            numbers = voiceProvider.listNumbers();
        } catch (Exception e) {
            log.warn("Provider health listNumbers failed: {}", e.getMessage());
            numbers = List.of();
        }
        return Map.of(
                "provider", voiceProvider.getName(),
                "configuredProvider", properties.getProvider(),
                "smsFallbackEnabled", properties.isSmsFallbackEnabled(),
                "numbers", numbers);
    }

    private String buildDefaultInstruction(VoiceCallPurpose purpose) {
        String clinic = properties.getDefaultClinicName();
        return switch (purpose) {
            case BOOKING -> "You are " + clinic
                    + "'s appointment scheduling assistant. Help the patient book a telehealth visit. "
                    + "Be concise and HIPAA-aware: do not discuss diagnoses. Confirm a specific date and time.";
            case REMINDER -> "You are " + clinic
                    + "'s reminder assistant. Remind the patient of their upcoming telehealth appointment "
                    + "and offer to reschedule if needed. Do not discuss clinical details.";
            case RESCHEDULE -> "You are " + clinic
                    + "'s scheduling assistant. Help the patient reschedule their telehealth appointment.";
            case FOLLOW_UP -> "You are " + clinic
                    + "'s follow-up assistant. Confirm the patient completed intake and is ready for their visit.";
        };
    }

    private static String normalizeStatus(String raw) {
        String s = raw.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (s) {
            case "in_progress" -> CommunicationStatus.IN_PROGRESS.name();
            case "no_answer" -> CommunicationStatus.NO_ANSWER.name();
            case "canceled" -> CommunicationStatus.CANCELLED.name();
            default -> s.toUpperCase(Locale.ROOT);
        };
    }

    private static Object firstNonNull(Object... values) {
        for (Object v : values) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object nested(Map<String, Object> map, String... path) {
        Object current = map;
        for (String key : path) {
            if (!(current instanceof Map<?, ?> m)) {
                return null;
            }
            current = m.get(key);
        }
        return current;
    }
}
