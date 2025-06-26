package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.*;

/**
 * SendNotificationCommand
 */
public record SendNotificationCommand(
    Long userId,
    UserRole recipientRole,
    NotificationType notificationType,
    NotificationChannel notificationChannel,
    String title,
    String message,
    Long activityId,
    Long cropId
) {
    public SendNotificationCommand(Long userId, UserRole recipientRole, NotificationType notificationType, 
                                 NotificationChannel notificationChannel, String title, String message) {
        this(userId, recipientRole, notificationType, notificationChannel, title, message, null, null);
    }
    
    // Constructor para compatibilidad con farmerId (deprecated)
    @Deprecated
    public static SendNotificationCommand forFarmer(Long farmerId, NotificationType notificationType, 
                                 NotificationChannel notificationChannel, String title, String message) {
        return new SendNotificationCommand(farmerId, UserRole.FARMER, notificationType, notificationChannel, title, message, null, null);
    }
}
