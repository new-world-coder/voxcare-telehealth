package com.voxcare.appointment.controller;

import com.voxcare.appointment.dto.AppointmentResponse;
import com.voxcare.appointment.dto.CreateAppointmentRequest;
import com.voxcare.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for appointment management
 */
@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Create a new appointment
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse appointment = appointmentService.createAppointment(request);
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    /**
     * Get appointment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Get appointments by patient ID
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments by provider ID
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByProvider(@PathVariable Long providerId) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByProvider(providerId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get upcoming appointments for a patient
     */
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointmentsByPatient(@PathVariable Long patientId) {
        List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get upcoming appointments for a provider
     */
    @GetMapping("/provider/{providerId}/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointmentsByProvider(@PathVariable Long providerId) {
        List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointmentsByProvider(providerId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Cancel an appointment
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id, 
            @RequestParam String reason) {
        AppointmentResponse appointment = appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Reschedule an appointment
     */
    @PostMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {
        AppointmentResponse appointment = appointmentService.rescheduleAppointment(id, newDate);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Start an appointment
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<AppointmentResponse> startAppointment(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.startAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Complete an appointment
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Mark appointment as no-show
     */
    @PostMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> markAsNoShow(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.markAsNoShow(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Get appointments in a date range
     */
    @GetMapping("/range")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsInDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments that need reminders
     */
    @GetMapping("/reminders")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsNeedingReminders() {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsNeedingReminders();
        return ResponseEntity.ok(appointments);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Appointment Service is healthy");
    }
}
