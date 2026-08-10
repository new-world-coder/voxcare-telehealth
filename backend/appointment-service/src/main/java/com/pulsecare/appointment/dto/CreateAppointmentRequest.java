package com.pulsecare.appointment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * DTO for creating a new appointment.
 * Accepts either appointmentDate + durationMinutes, or startTime + endTime (portal-friendly).
 */
public class CreateAppointmentRequest {

    private Long patientId;

    private Long providerId;

    @JsonAlias({"startTime", "start_time"})
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer durationMinutes;

    @JsonAlias({"end_time"})
    private LocalDateTime endTime;

    private String notes;

    public CreateAppointmentRequest() {}

    public void normalize() {
        if (appointmentDate != null && durationMinutes == null && endTime != null) {
            long minutes = ChronoUnit.MINUTES.between(appointmentDate, endTime);
            durationMinutes = (int) Math.max(minutes, 15);
        }
        if (appointmentDate != null && durationMinutes != null && endTime == null) {
            endTime = appointmentDate.plusMinutes(durationMinutes);
        }
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

    /** Portal alias setter for startTime JSON field. */
    public void setStartTime(LocalDateTime startTime) {
        this.appointmentDate = startTime;
    }

    public LocalDateTime getStartTime() {
        return appointmentDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
