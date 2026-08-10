package com.voxcare.notification.model;

/**
 * Enum representing the channels through which notifications can be sent
 */
public enum NotificationChannel {
    
    /**
     * Email notification
     */
    EMAIL,
    
    /**
     * SMS notification
     */
    SMS,
    
    /**
     * Push notification
     */
    PUSH,
    
    /**
     * In-app notification
     */
    IN_APP,
    
    /**
     * Webhook notification
     */
    WEBHOOK
}
