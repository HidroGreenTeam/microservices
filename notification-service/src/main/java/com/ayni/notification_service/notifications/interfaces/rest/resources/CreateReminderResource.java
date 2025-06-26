package com.ayni.notification_service.notifications.interfaces.rest.resources;

/**
 * CreateReminderResource
 */
public record CreateReminderResource(
    Long farmerId,
    String title,
    String message,
    String notificationChannel,
    String remindAt,
    Long activityId,
    Long cropId,
    boolean isRecurring,
    String recurrencePattern
) {}
