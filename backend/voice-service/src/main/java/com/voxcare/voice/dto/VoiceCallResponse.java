package com.voxcare.voice.dto;

import com.voxcare.voice.model.VoiceCall;
import com.voxcare.voice.model.VoiceCallPurpose;

import java.time.LocalDateTime;

public class VoiceCallResponse {

    private Long id;
    private String externalId;
    private String provider;
    private VoiceCallPurpose purpose;
    private Long patientId;
    private Long appointmentId;
    private Long providerId;
    private String toNumber;
    private String status;
    private String outcome;
    private Integer durationSeconds;
    private String transcript;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;

    public static VoiceCallResponse from(VoiceCall call) {
        VoiceCallResponse r = new VoiceCallResponse();
        r.id = call.getId();
        r.externalId = call.getExternalId();
        r.provider = call.getProvider();
        r.purpose = call.getPurpose();
        r.patientId = call.getPatientId();
        r.appointmentId = call.getAppointmentId();
        r.providerId = call.getProviderId();
        r.toNumber = call.getToNumber();
        r.status = call.getStatus();
        r.outcome = call.getOutcome();
        r.durationSeconds = call.getDurationSeconds();
        r.transcript = call.getTranscript();
        r.retryCount = call.getRetryCount();
        r.createdAt = call.getCreatedAt();
        r.endedAt = call.getEndedAt();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getProvider() {
        return provider;
    }

    public VoiceCallPurpose getPurpose() {
        return purpose;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getToNumber() {
        return toNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getOutcome() {
        return outcome;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public String getTranscript() {
        return transcript;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }
}
