package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import com.voxcare.voice.dto.InitiateVoiceCallRequest;
import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.model.VoiceCall;
import com.voxcare.voice.model.VoiceCallPurpose;
import com.voxcare.voice.provider.CallResult;
import com.voxcare.voice.provider.CommunicationStatus;
import com.voxcare.voice.provider.InitiateCallParams;
import com.voxcare.voice.provider.PhoneNumberInfo;
import com.voxcare.voice.provider.SendSmsParams;
import com.voxcare.voice.provider.SmsResult;
import com.voxcare.voice.provider.VoiceProvider;
import com.voxcare.voice.repository.VoiceCallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private final SchedulingContextClient schedulingContextClient;
    private final OutboundInstructionBuilder instructionBuilder;

    public VoiceCallService(
            VoiceProvider voiceProvider,
            VoiceCallRepository repository,
            VoiceProperties properties,
            PatientLookupClient patientLookupClient,
            SchedulingContextClient schedulingContextClient,
            OutboundInstructionBuilder instructionBuilder) {
        this.voiceProvider = voiceProvider;
        this.repository = repository;
        this.properties = properties;
        this.patientLookupClient = patientLookupClient;
        this.schedulingContextClient = schedulingContextClient;
        this.instructionBuilder = instructionBuilder;
    }

    @Transactional
    public VoiceCallResponse initiateCall(InitiateVoiceCallRequest request) {
        return initiateCall(request, 0);
    }

    @Transactional
    public VoiceCallResponse initiateCall(InitiateVoiceCallRequest request, int retryCount) {
        PatientInfo patient = request.getPatientId() == null
                ? null
                : patientLookupClient.findById(request.getPatientId());

        String to = request.getTo();
        if ((to == null || to.isBlank()) && patient != null) {
            to = patient.phone();
        }
        if (to == null || to.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phone number required (pass 'to' or a patientId with phone on file)");
        }

        String instruction = request.getOutboundInstruction();
        if (instruction == null || instruction.isBlank()) {
            List<OpenSlot> slots = schedulingContextClient.findOpenSlots(request.getProviderId());
            instruction = instructionBuilder.build(request.getPurpose(), patient, slots, null);
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
        call.setRetryCount(retryCount);
        call.setSmsFallbackSent(false);

        VoiceCall saved = repository.save(call);
        log.info("Voice call persisted id={} externalId={} provider={} retry={}",
                saved.getId(), saved.getExternalId(), saved.getProvider(), retryCount);
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

    /**
     * Pull appointments needing reminders and enqueue REMINDER voice calls.
     */
    @Transactional
    public Map<String, Object> enqueueReminderCalls() {
        List<ReminderAppointment> appointments = schedulingContextClient.findAppointmentsNeedingReminders();
        List<Long> createdCallIds = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();

        for (ReminderAppointment appointment : appointments) {
            if (appointment.patientId() == null) {
                skipped.add(appointment.id());
                continue;
            }
            try {
                InitiateVoiceCallRequest req = new InitiateVoiceCallRequest();
                req.setPatientId(appointment.patientId());
                req.setProviderId(appointment.providerId());
                req.setAppointmentId(appointment.id());
                req.setPurpose(VoiceCallPurpose.REMINDER);

                PatientInfo patient = patientLookupClient.findById(appointment.patientId());
                List<OpenSlot> slots = schedulingContextClient.findOpenSlots(appointment.providerId());
                req.setOutboundInstruction(instructionBuilder.build(
                        VoiceCallPurpose.REMINDER,
                        patient,
                        slots,
                        appointment.appointmentDate()));

                VoiceCallResponse created = initiateCall(req, 0);
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

    public void verifyWebhookSecret(String providedSecret) {
        String expected = properties.getDialWebhookSecret();
        if (expected == null || expected.isBlank()) {
            return; // open in local/dev when unset
        }
        if (providedSecret == null || providedSecret.isBlank() || !constantTimeEquals(expected, providedSecret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Dial webhook secret");
        }
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
            repository.save(call);
        } else if (isTerminalFailure(status)) {
            call.setOutcome(status);
            call.setEndedAt(LocalDateTime.now());
            repository.save(call);
            handleFailedCall(call);
        } else {
            repository.save(call);
        }

        log.info("Dial webhook applied externalId={} status={}", externalId, call.getStatus());
    }

    private void handleFailedCall(VoiceCall call) {
        int retries = call.getRetryCount() == null ? 0 : call.getRetryCount();
        if (retries < properties.getMaxRetries()) {
            log.info(
                    "Call id={} failed with retries {}/{}; use POST /voice/calls/{}/retry after {} minutes",
                    call.getId(),
                    retries,
                    properties.getMaxRetries(),
                    call.getId(),
                    properties.getRetryDelayMinutes());
        }

        if (properties.isSmsFallbackEnabled() && !Boolean.TRUE.equals(call.getSmsFallbackSent())) {
            sendSmsFallback(call);
        }
    }

    @Transactional
    public VoiceCallResponse retryCall(Long id) {
        VoiceCall existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice call not found"));
        int retries = existing.getRetryCount() == null ? 0 : existing.getRetryCount();
        if (retries >= properties.getMaxRetries()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max retries already reached");
        }
        InitiateVoiceCallRequest retry = new InitiateVoiceCallRequest();
        retry.setPatientId(existing.getPatientId());
        retry.setProviderId(existing.getProviderId());
        retry.setAppointmentId(existing.getAppointmentId());
        retry.setPurpose(existing.getPurpose());
        retry.setTo(existing.getToNumber());
        retry.setOutboundInstruction(existing.getOutboundInstruction());
        return initiateCall(retry, retries + 1);
    }

    private void sendSmsFallback(VoiceCall call) {
        try {
            String body = instructionBuilder.smsFallbackBody(call.getPurpose(), properties.getDefaultClinicName())
                    + " " + properties.getSmsFallbackBookingUrl();
            SmsResult sms = voiceProvider.sendSms(new SendSmsParams(
                    call.getToNumber(),
                    null,
                    body,
                    call.getPatientId()));
            call.setSmsFallbackSent(true);
            call.setSmsExternalId(sms.externalId() != null ? sms.externalId() : sms.messageId());
            repository.save(call);
            log.info("SMS fallback sent for call id={} smsId={}", call.getId(), call.getSmsExternalId());
        } catch (Exception e) {
            log.warn("SMS fallback failed for call id={}: {}", call.getId(), e.getMessage());
        }
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
        health.put("maxRetries", properties.getMaxRetries());
        health.put("webhookSecretConfigured",
                properties.getDialWebhookSecret() != null && !properties.getDialWebhookSecret().isBlank());
        health.put("numbers", numbers);
        return health;
    }

    private static boolean isTerminalFailure(String status) {
        return status.equals(CommunicationStatus.FAILED.name())
                || status.equals(CommunicationStatus.NO_ANSWER.name())
                || status.equals(CommunicationStatus.BUSY.name())
                || status.equals(CommunicationStatus.CANCELLED.name());
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

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }

    private static Object firstNonNull(Object... values) {
        for (Object v : values) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }

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
