package com.ayni.notification_service.notifications.interfaces.rest.resources;

/**
 * NotificationResource
 */
public record NotificationResource(
    Long id,
    Long profileId,
    String title,
    String message,
    String notificationType,
    String notificationChannel,
    String notificationStatus,
    String createdAt,
    String sentAt
) {
    public NotificationResource(Long id, String message) {
        this(id, null, null, message, null, null, null, null, null);
    }
}
