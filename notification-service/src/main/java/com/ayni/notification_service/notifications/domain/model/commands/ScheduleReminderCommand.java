package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;

import java.time.LocalDateTime;

/**
 * ScheduleReminderCommand
 */
public record ScheduleReminderCommand(
    Long profileId,
    NotificationChannel notificationChannel,
    String title,
    String message,
    LocalDateTime remindAt,
    Long activityId,
    Long cropId,
    boolean isRecurring,
    String recurrencePattern
) {
    public ScheduleReminderCommand(Long profileId, NotificationChannel notificationChannel, 
                                 String title, String message, LocalDateTime remindAt) {
        this(profileId, notificationChannel, title, message, remindAt, null, null, false, null);
    }
}
