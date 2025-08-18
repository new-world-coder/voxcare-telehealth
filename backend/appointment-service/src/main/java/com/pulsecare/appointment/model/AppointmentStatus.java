package com.pulsecare.appointment.model;

/**
 * Enum representing the possible states of an appointment
 */
public enum AppointmentStatus {
    
    /**
     * Appointment is scheduled but not yet started
     */
    SCHEDULED,
    
    /**
     * Appointment has been rescheduled to a new time
     */
    RESCHEDULED,
    
    /**
     * Appointment is currently in progress
     */
    IN_PROGRESS,
    
    /**
     * Appointment has been completed successfully
     */
    COMPLETED,
    
    /**
     * Appointment has been cancelled
     */
    CANCELLED,
    
    /**
     * Patient did not show up for the appointment
     */
    NO_SHOW,
    
    /**
     * Appointment was rescheduled by the provider
     */
    PROVIDER_RESCHEDULED,
    
    /**
     * Appointment was rescheduled by the patient
     */
    PATIENT_RESCHEDULED
}
