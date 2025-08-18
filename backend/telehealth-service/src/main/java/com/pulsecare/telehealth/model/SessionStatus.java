package com.pulsecare.telehealth.model;

/**
 * Enum representing the possible states of a telehealth session
 */
public enum SessionStatus {
    
    /**
     * Session is scheduled but not yet started
     */
    SCHEDULED,
    
    /**
     * Session is currently in progress
     */
    IN_PROGRESS,
    
    /**
     * Session has been completed successfully
     */
    COMPLETED,
    
    /**
     * Session has been cancelled
     */
    CANCELLED,
    
    /**
     * Session was rescheduled
     */
    RESCHEDULED,
    
    /**
     * Session failed to start or had technical issues
     */
    FAILED,
    
    /**
     * Session is waiting for participants to join
     */
    WAITING_FOR_PARTICIPANTS
}
