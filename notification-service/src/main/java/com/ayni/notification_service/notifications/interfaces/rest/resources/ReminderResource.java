package com.ayni.notification_service.notifications.interfaces.rest.resources;


public record ReminderResource(
    Long id,
    Long profileId,
    String title,
    String message,
    String notificationChannel,
    String remindAt,
    boolean isActive,
    boolean isRecurring,
    String recurrencePattern
) {
   
}
