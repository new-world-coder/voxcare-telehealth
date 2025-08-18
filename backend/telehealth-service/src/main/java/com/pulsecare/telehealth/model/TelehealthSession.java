package com.pulsecare.telehealth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Telehealth session entity representing a video consultation
 */
@Document(collection = "telehealth_sessions")
public class TelehealthSession {

    @Id
    private String id;

    @NotNull
    @Indexed
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long providerId;

    @NotNull
    private LocalDateTime scheduledStartTime;

    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;

    @NotNull
    private SessionStatus status;

    private String jitsiRoomId;
    private String jitsiRoomUrl;
    private String recordingUrl;

    private List<String> participants;
    private String notes;
    private String diagnosis;
    private String prescription;

    @NotNull
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TelehealthSession() {
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.SCHEDULED;
    }

    public TelehealthSession(Long appointmentId, Long patientId, Long providerId, LocalDateTime scheduledStartTime) {
        this();
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.providerId = providerId;
        this.scheduledStartTime = scheduledStartTime;
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
        this.updatedAt = LocalDateTime.now();
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

    // Business methods
    public void start() {
        this.status = SessionStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void end() {
        this.status = SessionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(this.actualStartTime, this.endTime).toMinutes();
        }
    }

    public void cancel() {
        this.status = SessionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == SessionStatus.IN_PROGRESS;
    }

    public boolean canBeStarted() {
        return this.status == SessionStatus.SCHEDULED;
    }

    public boolean canBeEnded() {
        return this.status == SessionStatus.IN_PROGRESS;
    }
}
