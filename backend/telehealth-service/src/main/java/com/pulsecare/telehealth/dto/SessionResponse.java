package com.pulsecare.telehealth.dto;

import com.pulsecare.telehealth.model.SessionStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for telehealth session responses
 */
public class SessionResponse {

    private String id;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private SessionStatus status;
    private String jitsiRoomId;
    private String jitsiRoomUrl;
    private String recordingUrl;
    private List<String> participants;
    private String notes;
    private String diagnosis;
    private String prescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public SessionResponse() {}

    public SessionResponse(String id, Long appointmentId, Long patientId, Long providerId, 
                         LocalDateTime scheduledStartTime, LocalDateTime actualStartTime, 
                         LocalDateTime endTime, Integer durationMinutes, SessionStatus status,
                         String jitsiRoomId, String jitsiRoomUrl, String recordingUrl,
                         List<String> participants, String notes, String diagnosis, 
                         String prescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.providerId = providerId;
        this.scheduledStartTime = scheduledStartTime;
        this.actualStartTime = actualStartTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.jitsiRoomId = jitsiRoomId;
        this.jitsiRoomUrl = jitsiRoomUrl;
        this.recordingUrl = recordingUrl;
        this.participants = participants;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getJitsiRoomId() {
        return jitsiRoomId;
    }

    public void setJitsiRoomId(String jitsiRoomId) {
        this.jitsiRoomId = jitsiRoomId;
    }

    public String getJitsiRoomUrl() {
        return jitsiRoomUrl;
    }

    public void setJitsiRoomUrl(String jitsiRoomUrl) {
        this.jitsiRoomUrl = jitsiRoomUrl;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
