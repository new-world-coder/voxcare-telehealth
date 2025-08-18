package com.pulsecare.appointment.dto;

import com.pulsecare.appointment.model.AppointmentStatus;
import java.time.LocalDateTime;

/**
 * DTO for appointment responses
 */
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private Long providerId;
    private LocalDateTime appointmentDate;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private String notes;
    private String telehealthUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cancellationReason;
    private LocalDateTime cancelledAt;

    // Constructors
    public AppointmentResponse() {}

    public AppointmentResponse(Long id, Long patientId, Long providerId, LocalDateTime appointmentDate, 
                             Integer durationMinutes, AppointmentStatus status, String notes, 
                             String telehealthUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
                             String cancellationReason, LocalDateTime cancelledAt) {
        this.id = id;
        this.patientId = patientId;
        this.providerId = providerId;
        this.appointmentDate = appointmentDate;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.notes = notes;
        this.telehealthUrl = telehealthUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cancellationReason = cancellationReason;
        this.cancelledAt = cancelledAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTelehealthUrl() {
        return telehealthUrl;
    }

    public void setTelehealthUrl(String telehealthUrl) {
        this.telehealthUrl = telehealthUrl;
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
