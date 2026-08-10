package com.voxcare.notification.model;

/**
 * Enum representing the types of notifications
 */
public enum NotificationType {
    
    /**
     * Appointment reminder
     */
    APPOINTMENT_REMINDER,
    
    /**
     * Appointment confirmation
     */
    APPOINTMENT_CONFIRMATION,
    
    /**
     * Appointment cancellation
     */
    APPOINTMENT_CANCELLATION,
    
    /**
     * Appointment rescheduled
     */
    APPOINTMENT_RESCHEDULED,
    
    /**
     * Telehealth session starting
     */
    TELEHEALTH_SESSION_STARTING,
    
    /**
     * Telehealth session link
     */
    TELEHEALTH_SESSION_LINK,
    
    /**
     * Prescription ready
     */
    PRESCRIPTION_READY,
    
    /**
     * Test results available
     */
    TEST_RESULTS_AVAILABLE,
    
    /**
     * Payment reminder
     */
    PAYMENT_REMINDER,
    
    /**
     * Payment confirmation
     */
    PAYMENT_CONFIRMATION,
    
    /**
     * General system notification
     */
    SYSTEM_NOTIFICATION,
    
    /**
     * Security alert
     */
    SECURITY_ALERT
}
