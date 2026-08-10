package com.voxcare.voice.dto;

import com.voxcare.voice.model.VoiceCallPurpose;
import jakarta.validation.constraints.NotNull;

public class InitiateVoiceCallRequest {

    private Long patientId;

    /** Optional when patientId is set and patient-service can resolve phone. */
    private String to;

    @NotNull
    private VoiceCallPurpose purpose;

    private Long providerId;

    private Long appointmentId;

    /** Optional override; otherwise a purpose-based template is used. */
    private String outboundInstruction;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public VoiceCallPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(VoiceCallPurpose purpose) {
        this.purpose = purpose;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getOutboundInstruction() {
        return outboundInstruction;
    }

    public void setOutboundInstruction(String outboundInstruction) {
        this.outboundInstruction = outboundInstruction;
    }
}
