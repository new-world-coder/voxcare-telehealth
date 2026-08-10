package com.pulsecare.appointment.service;

import com.pulsecare.appointment.dto.AppointmentResponse;
import com.pulsecare.appointment.dto.CreateAppointmentRequest;
import com.pulsecare.appointment.model.Appointment;
import com.pulsecare.appointment.model.AppointmentStatus;
import com.pulsecare.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for appointment business logic
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Create a new appointment
     */
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        request.normalize();
        if (request.getPatientId() == null || request.getProviderId() == null) {
            throw new RuntimeException("patientId and providerId are required");
        }
        if (request.getAppointmentDate() == null || request.getDurationMinutes() == null) {
            throw new RuntimeException("Provide appointmentDate/durationMinutes or startTime/endTime");
        }

        // Check for scheduling conflicts
        if (hasSchedulingConflict(request.getProviderId(), request.getAppointmentDate(), request.getDurationMinutes())) {
            throw new RuntimeException("Scheduling conflict detected for the requested time slot");
        }

        // Create appointment
        Appointment appointment = new Appointment(
                request.getPatientId(),
                request.getProviderId(),
                request.getAppointmentDate(),
                request.getDurationMinutes()
        );
        
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Check for scheduling conflicts
     */
    private boolean hasSchedulingConflict(Long providerId, LocalDateTime appointmentDate, Integer durationMinutes) {
        LocalDateTime startTime = appointmentDate;
        LocalDateTime endTime = appointmentDate.plus(durationMinutes, ChronoUnit.MINUTES);
        
        List<Appointment> conflictingAppointments = appointmentRepository.findActiveAppointmentsInTimeRange(
                providerId, startTime, endTime);
        
        return !conflictingAppointments.isEmpty();
    }

    /**
     * Get appointment by ID
     */
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return convertToResponse(appointment);
    }

    /**
     * Get appointments by patient ID
     */
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by provider ID
     */
    public List<AppointmentResponse> getAppointmentsByProvider(Long providerId) {
        return appointmentRepository.findByProviderIdOrderByAppointmentDateDesc(providerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming appointments for a patient
     */
    public List<AppointmentResponse> getUpcomingAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findUpcomingAppointmentsByPatient(patientId, LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming appointments for a provider
     */
    public List<AppointmentResponse> getUpcomingAppointmentsByProvider(Long providerId) {
        return appointmentRepository.findUpcomingAppointmentsByProvider(providerId, LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel an appointment
     */
    public AppointmentResponse cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (!appointment.canBeCancelled()) {
            throw new RuntimeException("Appointment cannot be cancelled. It must be scheduled and at least 24 hours in advance.");
        }

        appointment.cancel(reason);
        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Reschedule an appointment
     */
    public AppointmentResponse rescheduleAppointment(Long id, LocalDateTime newDate) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // Check for scheduling conflicts with the new time
        if (hasSchedulingConflict(appointment.getProviderId(), newDate, appointment.getDurationMinutes())) {
            throw new RuntimeException("Scheduling conflict detected for the new time slot");
        }

        appointment.reschedule(newDate);
        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Start an appointment
     */
    public AppointmentResponse startAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED && 
            appointment.getStatus() != AppointmentStatus.RESCHEDULED) {
            throw new RuntimeException("Appointment cannot be started. Current status: " + appointment.getStatus());
        }

        appointment.start();
        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Complete an appointment
     */
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Appointment cannot be completed. Current status: " + appointment.getStatus());
        }

        appointment.complete();
        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Mark appointment as no-show
     */
    public AppointmentResponse markAsNoShow(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED && 
            appointment.getStatus() != AppointmentStatus.RESCHEDULED) {
            throw new RuntimeException("Appointment cannot be marked as no-show. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment = appointmentRepository.save(appointment);
        return convertToResponse(appointment);
    }

    /**
     * Get appointments in a date range
     */
    public List<AppointmentResponse> getAppointmentsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findAppointmentsInDateRange(startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments that need reminders
     */
    public List<AppointmentResponse> getAppointmentsNeedingReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        return appointmentRepository.findAppointmentsNeedingReminders(tomorrow)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Appointment entity to DTO
     */
    private AppointmentResponse convertToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getProviderId(),
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getTelehealthUrl(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getCancellationReason(),
                appointment.getCancelledAt()
        );
    }
}
