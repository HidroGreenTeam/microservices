package com.ayni.notification_service.notifications.interfaces.rest.resources;

/**
 * ReminderResource
 */
public record ReminderResource(
    Long id,
    Long farmerId,
    String title,
    String message,
    String notificationChannel,
    String remindAt,
    boolean isActive,
    boolean isRecurring,
    String recurrencePattern
) {
    public ReminderResource(Long id, String message) {
        this(id, null, null, message, null, null, false, false, null);
    }
}
