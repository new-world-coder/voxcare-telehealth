package com.pulsecare.telehealth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "telehealth_sessions")
public class TelehealthSession {
    
    @Id
    private String id;
    
    @Indexed
    private Long appointmentId;
    
    private String roomSlug;
    
    private String roomUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime endedAt;
    
    private String notesNonPHI;
    
    private SessionStatus status;
    
    public enum SessionStatus {
        ACTIVE, ENDED, CANCELLED
    }
    
    // Constructors
    public TelehealthSession() {
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }
    
    public TelehealthSession(Long appointmentId, String roomSlug, String roomUrl) {
        this();
        this.appointmentId = appointmentId;
        this.roomSlug = roomSlug;
        this.roomUrl = roomUrl;
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
    
    public String getRoomSlug() {
        return roomSlug;
    }
    
    public void setRoomSlug(String roomSlug) {
        this.roomSlug = roomSlug;
    }
    
    public String getRoomUrl() {
        return roomUrl;
    }
    
    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
    
    public String getNotesNonPHI() {
        return notesNonPHI;
    }
    
    public void setNotesNonPHI(String notesNonPHI) {
        this.notesNonPHI = notesNonPHI;
    }
    
    public SessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
