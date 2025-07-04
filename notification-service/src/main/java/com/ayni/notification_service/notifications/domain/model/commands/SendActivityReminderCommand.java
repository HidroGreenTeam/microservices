package com.ayni.notification_service.notifications.domain.model.commands;

import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;


public record SendActivityReminderCommand(
    Long profileId,
    Long activityId,
    NotificationChannel notificationChannel,
    String activityTitle,
    String reminderMessage
) {}
