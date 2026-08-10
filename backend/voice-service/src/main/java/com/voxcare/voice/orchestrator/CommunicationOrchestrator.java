package com.voxcare.voice.orchestrator;

import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.model.ScheduledFollowUp;
import com.voxcare.voice.model.VoiceCall;
import com.voxcare.voice.model.VoiceCallPurpose;
import com.voxcare.voice.provider.CallResult;
import com.voxcare.voice.provider.CallStatusResult;
import com.voxcare.voice.provider.CommunicationStatus;
import com.voxcare.voice.provider.InitiateCallParams;
import com.voxcare.voice.provider.SendSmsParams;
import com.voxcare.voice.provider.SmsResult;
import com.voxcare.voice.provider.VoiceProvider;
import com.voxcare.voice.repository.ScheduledFollowUpRepository;
import com.voxcare.voice.repository.VoiceCallRepository;
import com.voxcare.voice.repository.VoiceRuleRepository;
import com.voxcare.voice.service.OpenSlot;
import com.voxcare.voice.service.OutboundInstructionBuilder;
import com.voxcare.voice.service.PatientInfo;
import com.voxcare.voice.service.PatientLookupClient;
import com.voxcare.voice.service.SchedulingContextClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Faithful port of EstateCraft CommunicationOrchestrator for Dial voice + SMS fallback.
 */
@Service
public class CommunicationOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(CommunicationOrchestrator.class);

    private final VoiceProvider voiceProvider;
    private final VoiceCallRepository voiceCallRepository;
    private final VoiceRuleRepository voiceRuleRepository;
    private final ScheduledFollowUpRepository followUpRepository;
    private final PatientLookupClient patientLookupClient;
    private final SchedulingContextClient schedulingContextClient;
    private final OutboundInstructionBuilder instructionBuilder;

    public CommunicationOrchestrator(
            VoiceProvider voiceProvider,
            VoiceCallRepository voiceCallRepository,
            VoiceRuleRepository voiceRuleRepository,
            ScheduledFollowUpRepository followUpRepository,
            PatientLookupClient patientLookupClient,
            SchedulingContextClient schedulingContextClient,
            OutboundInstructionBuilder instructionBuilder) {
        this.voiceProvider = voiceProvider;
        this.voiceCallRepository = voiceCallRepository;
        this.voiceRuleRepository = voiceRuleRepository;
        this.followUpRepository = followUpRepository;
        this.patientLookupClient = patientLookupClient;
        this.schedulingContextClient = schedulingContextClient;
        this.instructionBuilder = instructionBuilder;
    }

    public VoiceRuleConfig resolveActiveRule() {
        return voiceRuleRepository.findFirstByEnabledTrueOrderByPriorityDesc()
                .map(VoiceRuleConfig::from)
                .orElseGet(VoiceRuleConfig::defaultRule);
    }

    /**
     * EstateCraft-equivalent of triggerOutboundCall(lead, rule, retryCount, parentId).
     */
    @Transactional
    public VoiceCallResponse triggerOutboundCall(
            Long patientId,
            String toOverride,
            Long providerId,
            Long appointmentId,
            VoiceCallPurpose purpose,
            VoiceRuleConfig rule,
            int retryCount,
            Long parentCallId) {

        PatientInfo patient = patientId == null ? null : patientLookupClient.findById(patientId);
        String to = toOverride;
        if ((to == null || to.isBlank()) && patient != null) {
            to = patient.phone();
        }
        if (to == null || to.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Patient has no phone number for voice outreach");
        }

        List<OpenSlot> slots = schedulingContextClient.findOpenSlots(providerId);
        String slotBlock = OutboundInstructionBuilder.formatSlots(slots);
        String instruction = rule.renderInstruction(patient);
        // Append live open slots (VoxCare booking context) while keeping EstateCraft template personalization
        if (!instruction.contains(slotBlock) && purpose == VoiceCallPurpose.BOOKING) {
            instruction = instruction + "\nAvailable slots:\n" + slotBlock;
        }

        try {
            CallResult result = voiceProvider.initiateCall(new InitiateCallParams(
                    to, null, instruction, patientId, purpose.name()));

            VoiceCall call = new VoiceCall();
            call.setExternalId(result.externalId() != null ? result.externalId() : result.callId());
            call.setProvider(result.provider());
            call.setPurpose(purpose);
            call.setPatientId(patientId);
            call.setProviderId(providerId);
            call.setAppointmentId(appointmentId);
            call.setToNumber(to);
            call.setStatus(result.status().name());
            call.setOutboundInstruction(instruction);
            call.setRetryCount(retryCount);
            call.setSmsFallbackSent(false);
            VoiceCall saved = voiceCallRepository.save(call);

            log.info("Outbound call triggered patientId={} callId={} provider={} retry={}",
                    patientId, saved.getId(), saved.getProvider(), retryCount);
            return VoiceCallResponse.from(saved);
        } catch (Exception error) {
            VoiceCall failed = new VoiceCall();
            failed.setExternalId("failed_" + System.currentTimeMillis());
            failed.setProvider(voiceProvider.getName());
            failed.setPurpose(purpose);
            failed.setPatientId(patientId);
            failed.setProviderId(providerId);
            failed.setAppointmentId(appointmentId);
            failed.setToNumber(to);
            failed.setStatus(CommunicationStatus.FAILED.name());
            failed.setOutcome(CommunicationStatus.FAILED.name());
            failed.setOutboundInstruction(instruction);
            failed.setRetryCount(retryCount);
            failed.setEndedAt(LocalDateTime.now());
            failed.setSmsFallbackSent(false);
            VoiceCall saved = voiceCallRepository.save(failed);

            String reason = error.getMessage() == null ? "Unknown error" : error.getMessage();
            if (retryCount < rule.maxRetries()) {
                scheduleRetry(patientId, rule, retryCount + 1, saved.getId());
            } else if (rule.smsFallbackEnabled()) {
                triggerSmsFallback(patient, to, rule, saved);
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Outbound call failed: " + reason + ". Check Dial credentials and phone format.");
        }
    }

    @Transactional
    public VoiceCallResponse pollCallStatus(Long callId) {
        VoiceCall call = voiceCallRepository.findById(callId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Communication not found"));
        if (call.getExternalId() == null || call.getExternalId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Call has no provider reference");
        }

        CallStatusResult status = voiceProvider.getCallStatus(call.getExternalId());
        call.setStatus(status.status().name());
        if (status.outcome() != null) {
            call.setOutcome(status.outcome().name());
        }
        if (status.durationSeconds() != null) {
            call.setDurationSeconds(status.durationSeconds());
        }
        if (status.transcript() != null) {
            call.setTranscript(status.transcript());
        }

        String statusStr = call.getStatus().toUpperCase(Locale.ROOT);
        if (List.of("COMPLETED", "FAILED", "NO_ANSWER", "BUSY", "CANCELLED").contains(statusStr)) {
            call.setEndedAt(LocalDateTime.now());
            if (List.of("NO_ANSWER", "BUSY", "FAILED").contains(statusStr)) {
                VoiceRuleConfig rule = resolveActiveRule();
                int retries = call.getRetryCount() == null ? 0 : call.getRetryCount();
                if (retries < rule.maxRetries()) {
                    scheduleRetry(call.getPatientId(), rule, retries + 1, call.getId());
                } else if (rule.smsFallbackEnabled()) {
                    PatientInfo patient = call.getPatientId() == null
                            ? null
                            : patientLookupClient.findById(call.getPatientId());
                    triggerSmsFallback(patient, call.getToNumber(), rule, call);
                }
            }
        }

        return VoiceCallResponse.from(voiceCallRepository.save(call));
    }

    @Transactional
    public void handleProviderWebhook(VoiceCall call, String status, Integer duration, String transcript) {
        call.setStatus(status);
        if (duration != null) {
            call.setDurationSeconds(duration);
        }
        if (transcript != null) {
            call.setTranscript(transcript);
        }
        String statusStr = status.toUpperCase(Locale.ROOT);
        if ("COMPLETED".equals(statusStr)) {
            call.setOutcome("CONNECTED");
            call.setEndedAt(LocalDateTime.now());
            voiceCallRepository.save(call);
            return;
        }
        if (List.of("FAILED", "NO_ANSWER", "BUSY", "CANCELLED").contains(statusStr)) {
            call.setOutcome(statusStr);
            call.setEndedAt(LocalDateTime.now());
            voiceCallRepository.save(call);
            VoiceRuleConfig rule = resolveActiveRule();
            int retries = call.getRetryCount() == null ? 0 : call.getRetryCount();
            if (retries < rule.maxRetries()) {
                scheduleRetry(call.getPatientId(), rule, retries + 1, call.getId());
            } else if (rule.smsFallbackEnabled()) {
                PatientInfo patient = call.getPatientId() == null
                        ? null
                        : patientLookupClient.findById(call.getPatientId());
                triggerSmsFallback(patient, call.getToNumber(), rule, call);
            }
            return;
        }
        voiceCallRepository.save(call);
    }

    private void scheduleRetry(Long patientId, VoiceRuleConfig rule, int retryCount, Long parentCallId) {
        ScheduledFollowUp followUp = new ScheduledFollowUp();
        followUp.setPatientId(patientId);
        followUp.setScheduledAt(LocalDateTime.now().plusMinutes(rule.retryDelayMinutes()));
        followUp.setType("voice_retry");
        followUp.setParentCallId(parentCallId);
        followUp.setNotes("Retry #" + retryCount + " for call " + parentCallId);
        followUp.setProcessed(false);
        followUpRepository.save(followUp);
        log.info("Voice retry scheduled patientId={} retryCount={} at={}",
                patientId, retryCount, followUp.getScheduledAt());
    }

    private void triggerSmsFallback(PatientInfo patient, String to, VoiceRuleConfig rule, VoiceCall parent) {
        if (to == null || to.isBlank() || Boolean.TRUE.equals(parent.getSmsFallbackSent())) {
            return;
        }
        try {
            String body = rule.renderSms(patient);
            SmsResult sms = voiceProvider.sendSms(new SendSmsParams(to, null, body, parent.getPatientId()));
            parent.setSmsFallbackSent(true);
            parent.setSmsExternalId(sms.externalId() != null ? sms.externalId() : sms.messageId());
            voiceCallRepository.save(parent);
            log.info("SMS fallback sent parentCallId={} smsId={}", parent.getId(), parent.getSmsExternalId());
        } catch (Exception e) {
            log.error("SMS fallback failed parentCallId={}: {}", parent.getId(), e.getMessage());
        }
    }

    @Transactional
    public int processDueRetries() {
        List<ScheduledFollowUp> due = followUpRepository
                .findByProcessedFalseAndScheduledAtLessThanEqualOrderByScheduledAtAsc(LocalDateTime.now());
        int processed = 0;
        VoiceRuleConfig rule = resolveActiveRule();
        for (ScheduledFollowUp followUp : due) {
            try {
                VoiceCall parent = followUp.getParentCallId() == null
                        ? null
                        : voiceCallRepository.findById(followUp.getParentCallId()).orElse(null);
                int nextRetry = parent == null || parent.getRetryCount() == null ? 1 : parent.getRetryCount() + 1;
                triggerOutboundCall(
                        followUp.getPatientId(),
                        parent == null ? null : parent.getToNumber(),
                        parent == null ? null : parent.getProviderId(),
                        parent == null ? null : parent.getAppointmentId(),
                        parent == null ? VoiceCallPurpose.BOOKING : parent.getPurpose(),
                        rule,
                        nextRetry,
                        followUp.getParentCallId());
                followUp.setProcessed(true);
                followUpRepository.save(followUp);
                processed++;
            } catch (Exception e) {
                log.warn("Due retry failed id={}: {}", followUp.getId(), e.getMessage());
                followUp.setProcessed(true);
                followUpRepository.save(followUp);
            }
        }
        return processed;
    }
}
