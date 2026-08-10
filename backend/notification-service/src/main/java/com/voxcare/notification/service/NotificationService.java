package com.voxcare.notification.service;

import com.voxcare.notification.dto.NotificationResponse;
import com.voxcare.notification.dto.SendNotificationRequest;
import com.voxcare.notification.model.Notification;
import com.voxcare.notification.model.NotificationChannel;
import com.voxcare.notification.model.NotificationStatus;
import com.voxcare.notification.model.NotificationType;
import com.voxcare.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for notification business logic
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, JavaMailSender emailSender) {
        this.notificationRepository = notificationRepository;
        this.emailSender = emailSender;
    }

    /**
     * Send a notification
     */
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        // Create notification
        Notification notification = new Notification(
                request.getUserId(),
                request.getType(),
                request.getChannel(),
                request.getSubject(),
                request.getMessage()
        );

        // Set recipient information
        if (request.getRecipientEmail() != null) {
            notification.setRecipientEmail(request.getRecipientEmail());
        }
        if (request.getRecipientPhone() != null) {
            notification.setRecipientPhone(request.getRecipientPhone());
        }
        if (request.getMetadata() != null) {
            notification.setMetadata(request.getMetadata());
        }

        // Save notification
        notification = notificationRepository.save(notification);

        // Send notification based on channel
        try {
            sendNotificationByChannel(notification);
            notification.markAsSent();
        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
        }

        notification = notificationRepository.save(notification);
        return convertToResponse(notification);
    }

    /**
     * Send notification based on channel
     */
    private void sendNotificationByChannel(Notification notification) {
        switch (notification.getChannel()) {
            case EMAIL:
                sendEmailNotification(notification);
                break;
            case SMS:
                sendSmsNotification(notification);
                break;
            case PUSH:
                sendPushNotification(notification);
                break;
            case IN_APP:
                // In-app notifications are just stored, no external service needed
                break;
            case WEBHOOK:
                sendWebhookNotification(notification);
                break;
            default:
                throw new UnsupportedOperationException("Channel not supported: " + notification.getChannel());
        }
    }

    /**
     * Send email notification
     */
    private void sendEmailNotification(Notification notification) {
        if (notification.getRecipientEmail() == null) {
            throw new RuntimeException("Recipient email is required for email notifications");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(notification.getRecipientEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());

        emailSender.send(message);
    }

    /**
     * Send SMS notification (placeholder implementation)
     */
    private void sendSmsNotification(Notification notification) {
        if (notification.getRecipientPhone() == null) {
            throw new RuntimeException("Recipient phone is required for SMS notifications");
        }

        // TODO: Integrate with SMS service (Twilio, AWS SNS, etc.)
        // For now, just log the SMS
        System.out.println("SMS to " + notification.getRecipientPhone() + ": " + notification.getMessage());
    }

    /**
     * Send push notification (placeholder implementation)
     */
    private void sendPushNotification(Notification notification) {
        // TODO: Integrate with push notification service (Firebase, AWS SNS, etc.)
        // For now, just log the push notification
        System.out.println("Push notification to user " + notification.getUserId() + ": " + notification.getMessage());
    }

    /**
     * Send webhook notification (placeholder implementation)
     */
    private void sendWebhookNotification(Notification notification) {
        // TODO: Integrate with webhook service
        // For now, just log the webhook
        System.out.println("Webhook notification: " + notification.getMessage());
    }

    /**
     * Get notification by ID
     */
    public NotificationResponse getNotificationById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return convertToResponse(notification);
    }

    /**
     * Get notifications by user ID
     */
    public List<NotificationResponse> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications by type
     */
    public List<NotificationResponse> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications by status
     */
    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        if (status == NotificationStatus.PENDING) {
            return notificationRepository.findPendingNotifications()
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            return notificationRepository.findByStatusOrderByCreatedAtDesc(status)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get unread notifications for a user
     */
    public List<NotificationResponse> getUnreadNotificationsByUser(String userId) {
        return notificationRepository.findUnreadNotificationsByUser(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Count unread notifications for a user
     */
    public long countUnreadNotificationsByUser(String userId) {
        return notificationRepository.countUnreadNotificationsByUser(userId);
    }

    /**
     * Mark notification as read
     */
    public NotificationResponse markNotificationAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsRead();
        notification = notificationRepository.save(notification);
        return convertToResponse(notification);
    }

    /**
     * Mark all notifications as read for a user
     */
    public void markAllNotificationsAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotificationsByUser(userId);
        unreadNotifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Get notifications in a date range
     */
    public List<NotificationResponse> getNotificationsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findNotificationsInDateRange(startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications by user in a date range
     */
    public List<NotificationResponse> getNotificationsByUserInDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findNotificationsByUserInDateRange(userId, startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retry failed notifications
     */
    public void retryFailedNotifications() {
        LocalDateTime retryTime = LocalDateTime.now().minusHours(1); // Retry after 1 hour
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry(retryTime);

        for (Notification notification : failedNotifications) {
            try {
                notification.setStatus(NotificationStatus.PENDING);
                notificationRepository.save(notification);
                
                sendNotificationByChannel(notification);
                notification.markAsSent();
            } catch (Exception e) {
                notification.markAsFailed(e.getMessage());
            }
            notificationRepository.save(notification);
        }
    }

    /**
     * Clean up old notifications
     */
    public void cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        notificationRepository.deleteOldNotifications(cutoffDate);
    }

    /**
     * Convert Notification entity to DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getChannel(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getRecipientEmail(),
                notification.getRecipientPhone(),
                notification.getMetadata(),
                notification.getStatus(),
                notification.getSentAt(),
                notification.getReadAt(),
                notification.getErrorMessage(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
