package com.pulsecare.notification.repository;

import com.pulsecare.notification.model.Notification;
import com.pulsecare.notification.model.NotificationChannel;
import com.pulsecare.notification.model.NotificationStatus;
import com.pulsecare.notification.model.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for notification data access
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Find notifications by user ID
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Find notifications by type
     */
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);

    /**
     * Find notifications by channel
     */
    List<Notification> findByChannelOrderByCreatedAtDesc(NotificationChannel channel);

    /**
     * Find notifications by status
     */
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    /**
     * Find notifications by user ID and status
     */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, NotificationStatus status);

    /**
     * Find notifications by user ID and type
     */
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, NotificationType type);

    /**
     * Find pending notifications
     */
    @Query("{'status': 'PENDING'}")
    List<Notification> findPendingNotifications();

    /**
     * Find failed notifications
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);

    /**
     * Find notifications within a date range
     */
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Notification> findNotificationsInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find notifications by user ID in a date range
     */
    @Query("{'userId': ?0, 'createdAt': {$gte: ?1, $lte: ?2}}")
    List<Notification> findNotificationsByUserInDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find unread notifications for a user
     */
    @Query("{'userId': ?0, 'readAt': {$exists: false}}")
    List<Notification> findUnreadNotificationsByUser(String userId);

    /**
     * Count unread notifications for a user
     */
    @Query(value = "{'userId': ?0, 'readAt': {$exists: false}}", count = true)
    long countUnreadNotificationsByUser(String userId);

    /**
     * Find notifications by recipient email
     */
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    /**
     * Find notifications by recipient phone
     */
    List<Notification> findByRecipientPhoneOrderByCreatedAtDesc(String recipientPhone);

    /**
     * Find notifications that need to be retried (failed notifications)
     */
    @Query("{'status': 'FAILED', 'updatedAt': {$lte: ?0}}")
    List<Notification> findFailedNotificationsForRetry(LocalDateTime retryTime);

    /**
     * Delete old notifications (cleanup)
     */
    @Query(value = "{'createdAt': {$lt: ?0}}", delete = true)
    void deleteOldNotifications(LocalDateTime cutoffDate);
}
