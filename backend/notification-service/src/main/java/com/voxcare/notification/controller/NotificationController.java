package com.voxcare.notification.controller;

import com.voxcare.notification.dto.NotificationResponse;
import com.voxcare.notification.dto.SendNotificationRequest;
import com.voxcare.notification.model.NotificationChannel;
import com.voxcare.notification.model.NotificationStatus;
import com.voxcare.notification.model.NotificationType;
import com.voxcare.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for notification management
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Send a notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody SendNotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable String id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(@PathVariable String userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(@PathVariable NotificationType type) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUser(@PathVariable String userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Count unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotificationsByUser(@PathVariable String userId) {
        long count = notificationService.countUnreadNotificationsByUser(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable String id) {
        NotificationResponse response = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get notifications in a date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<NotificationResponse>> getNotificationsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<NotificationResponse> notifications = notificationService.getNotificationsInDateRange(startDate, endDate);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by user in a date range
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserInDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserInDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Retry failed notifications
     */
    @PostMapping("/retry-failed")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }

    /**
     * Clean up old notifications
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldNotifications(@RequestParam(defaultValue = "90") int daysToKeep) {
        notificationService.cleanupOldNotifications(daysToKeep);
        return ResponseEntity.ok().build();
    }

    /**
     * Get notification types
     */
    @GetMapping("/types")
    public ResponseEntity<NotificationType[]> getNotificationTypes() {
        return ResponseEntity.ok(NotificationType.values());
    }

    /**
     * Get notification channels
     */
    @GetMapping("/channels")
    public ResponseEntity<NotificationChannel[]> getNotificationChannels() {
        return ResponseEntity.ok(NotificationChannel.values());
    }

    /**
     * Get notification statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<NotificationStatus[]> getNotificationStatuses() {
        return ResponseEntity.ok(NotificationStatus.values());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
