package com.voxcare.notification.model;

/**
 * Enum representing the status of a notification
 */
public enum NotificationStatus {
    
    /**
     * Notification is pending to be sent
     */
    PENDING,
    
    /**
     * Notification is currently being processed
     */
    PROCESSING,
    
    /**
     * Notification has been sent successfully
     */
    SENT,
    
    /**
     * Notification failed to send
     */
    FAILED,
    
    /**
     * Notification has been cancelled
     */
    CANCELLED,
    
    /**
     * Notification has been read by the recipient
     */
    READ
}
