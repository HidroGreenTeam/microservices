package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.UserRole;

/**
 * SendActivityReminderCommand
 */
public record SendActivityReminderCommand(
    Long userId,
    UserRole recipientRole,
    Long activityId,
    NotificationChannel notificationChannel,
    String activityTitle,
    String reminderMessage
) {
    // Constructor para compatibilidad con farmerId (deprecated)
    @Deprecated
    public static SendActivityReminderCommand forFarmer(Long farmerId, Long activityId, 
                                                      NotificationChannel notificationChannel,
                                                      String activityTitle, String reminderMessage) {
        return new SendActivityReminderCommand(farmerId, UserRole.FARMER, activityId, notificationChannel, activityTitle, reminderMessage);
    }
}
