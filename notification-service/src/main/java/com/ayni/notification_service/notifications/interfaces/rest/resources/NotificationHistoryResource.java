package com.ayni.notification_service.notifications.interfaces.rest.resources;

/**
 * NotificationHistoryResource
 */
public record NotificationHistoryResource(
    Long notificationId,
    Long profileId,
    String title,
    String message,
    String notificationType,
    String notificationChannel,
    String notificationStatus,
    String createdAt,
    String sentAt,
    String deliveredAt,
    Long activityId,
    Long cropId
) {}
