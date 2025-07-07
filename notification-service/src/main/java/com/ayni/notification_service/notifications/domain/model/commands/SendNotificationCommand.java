package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.*;

/**
 * SendNotificationCommand
 */
public record SendNotificationCommand(
    Long profileId,
    NotificationType notificationType,
    NotificationChannel notificationChannel,
    String title,
    String message
) {
}
