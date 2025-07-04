package com.ayni.notification_service.notifications.interfaces.rest.resources;


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
    
}
