package com.ayni.notification_service.notifications.interfaces.rest.resources;

/**
 * SendNotificationResource
 */
public record SendNotificationResource(
    Long profileId,
    String recipient,  // Email or phone number
    String subject,    // For email notifications
    String title,
    String message,
    String notificationType,
    String notificationChannel,
    Long activityId,
    Long cropId
) {}
