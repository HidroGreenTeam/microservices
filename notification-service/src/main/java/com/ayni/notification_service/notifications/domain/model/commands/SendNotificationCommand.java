package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.*;


public record SendNotificationCommand(
    Long profileId,
    NotificationType notificationType,
    NotificationChannel notificationChannel,
    String title,
    String message,
    Long activityId,
    Long cropId
) {
    public SendNotificationCommand(Long profileId, NotificationType notificationType, 
                                 NotificationChannel notificationChannel, String title, String message) {
        this(profileId, notificationType, notificationChannel, title, message, null, null);
    }
}
