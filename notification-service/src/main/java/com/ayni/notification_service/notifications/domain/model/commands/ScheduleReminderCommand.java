package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;

import java.time.LocalDateTime;

/**
 * ScheduleReminderCommand
 */
public record ScheduleReminderCommand(
    Long farmerId,
    NotificationChannel notificationChannel,
    String title,
    String message,
    LocalDateTime remindAt,
    Long activityId,
    Long cropId,
    boolean isRecurring,
    String recurrencePattern
) {
    public ScheduleReminderCommand(Long farmerId, NotificationChannel notificationChannel, 
                                 String title, String message, LocalDateTime remindAt) {
        this(farmerId, notificationChannel, title, message, remindAt, null, null, false, null);
    }
}
