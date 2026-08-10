package com.voxcare.voice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Scheduled voice retry — mirrors EstateCraft scheduled_follow_ups (type=voice_retry).
 */
@Entity
@Table(name = "scheduled_follow_ups")
public class ScheduledFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false, length = 64)
    private String type = "voice_retry";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "parent_call_id")
    private Long parentCallId;

    @Column(nullable = false)
    private Boolean processed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (processed == null) processed = false;
        if (type == null || type.isBlank()) type = "voice_retry";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getParentCallId() { return parentCallId; }
    public void setParentCallId(Long parentCallId) { this.parentCallId = parentCallId; }
    public Boolean getProcessed() { return processed; }
    public void setProcessed(Boolean processed) { this.processed = processed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
