package com.pulsecare.telehealth.controller;

import com.pulsecare.telehealth.dto.CreateSessionRequest;
import com.pulsecare.telehealth.dto.SessionResponse;
import com.pulsecare.telehealth.service.TelehealthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for telehealth session management
 */
@RestController
@RequestMapping("/telehealth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TelehealthController {

    private final TelehealthService telehealthService;

    @Autowired
    public TelehealthController(TelehealthService telehealthService) {
        this.telehealthService = telehealthService;
    }

    /**
     * Create a new telehealth session
     */
    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        SessionResponse session = telehealthService.createSession(request);
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    /**
     * Get session by ID
     */
    @GetMapping("/sessions/{id}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable String id) {
        SessionResponse session = telehealthService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    /**
     * Get session by appointment ID
     */
    @GetMapping("/sessions/appointment/{appointmentId}")
    public ResponseEntity<SessionResponse> getSessionByAppointmentId(@PathVariable Long appointmentId) {
        SessionResponse session = telehealthService.getSessionByAppointmentId(appointmentId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get sessions by patient ID
     */
    @GetMapping("/sessions/patient/{patientId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByPatient(@PathVariable Long patientId) {
        List<SessionResponse> sessions = telehealthService.getSessionsByPatient(patientId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get sessions by provider ID
     */
    @GetMapping("/sessions/provider/{providerId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByProvider(@PathVariable Long providerId) {
        List<SessionResponse> sessions = telehealthService.getSessionsByProvider(providerId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get upcoming sessions for a patient
     */
    @GetMapping("/sessions/patient/{patientId}/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessionsByPatient(@PathVariable Long patientId) {
        List<SessionResponse> sessions = telehealthService.getUpcomingSessionsByPatient(patientId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get upcoming sessions for a provider
     */
    @GetMapping("/sessions/provider/{providerId}/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessionsByProvider(@PathVariable Long providerId) {
        List<SessionResponse> sessions = telehealthService.getUpcomingSessionsByProvider(providerId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Start a session
     */
    @PostMapping("/sessions/{id}/start")
    public ResponseEntity<SessionResponse> startSession(@PathVariable String id) {
        SessionResponse session = telehealthService.startSession(id);
        return ResponseEntity.ok(session);
    }

    /**
     * End a session
     */
    @PostMapping("/sessions/{id}/end")
    public ResponseEntity<SessionResponse> endSession(@PathVariable String id) {
        SessionResponse session = telehealthService.endSession(id);
        return ResponseEntity.ok(session);
    }

    /**
     * Cancel a session
     */
    @PostMapping("/sessions/{id}/cancel")
    public ResponseEntity<SessionResponse> cancelSession(@PathVariable String id) {
        SessionResponse session = telehealthService.cancelSession(id);
        return ResponseEntity.ok(session);
    }

    /**
     * Join a session
     */
    @PostMapping("/sessions/{id}/join")
    public ResponseEntity<SessionResponse> joinSession(
            @PathVariable String id, 
            @RequestParam String participantId) {
        SessionResponse session = telehealthService.joinSession(id, participantId);
        return ResponseEntity.ok(session);
    }

    /**
     * Update session notes
     */
    @PutMapping("/sessions/{id}/notes")
    public ResponseEntity<SessionResponse> updateNotes(
            @PathVariable String id, 
            @RequestParam String notes) {
        SessionResponse session = telehealthService.updateNotes(id, notes);
        return ResponseEntity.ok(session);
    }

    /**
     * Update session diagnosis
     */
    @PutMapping("/sessions/{id}/diagnosis")
    public ResponseEntity<SessionResponse> updateDiagnosis(
            @PathVariable String id, 
            @RequestParam String diagnosis) {
        SessionResponse session = telehealthService.updateDiagnosis(id, diagnosis);
        return ResponseEntity.ok(session);
    }

    /**
     * Update session prescription
     */
    @PutMapping("/sessions/{id}/prescription")
    public ResponseEntity<SessionResponse> updatePrescription(
            @PathVariable String id, 
            @RequestParam String prescription) {
        SessionResponse session = telehealthService.updatePrescription(id, prescription);
        return ResponseEntity.ok(session);
    }

    /**
     * Get sessions in a date range
     */
    @GetMapping("/sessions/range")
    public ResponseEntity<List<SessionResponse>> getSessionsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SessionResponse> sessions = telehealthService.getSessionsInDateRange(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get sessions ready to start
     */
    @GetMapping("/sessions/ready")
    public ResponseEntity<List<SessionResponse>> getSessionsReadyToStart() {
        List<SessionResponse> sessions = telehealthService.getSessionsReadyToStart();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Telehealth Service is healthy");
    }
}
