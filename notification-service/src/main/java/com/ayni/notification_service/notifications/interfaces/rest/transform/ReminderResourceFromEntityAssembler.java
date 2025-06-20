package com.ayni.notification_service.notifications.interfaces.rest.transform;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.interfaces.rest.resources.ReminderResource;

/**
 * ReminderResourceFromEntityAssembler
 */
public class ReminderResourceFromEntityAssembler {
    
    public static ReminderResource toResourceFromEntity(Reminder entity) {
        return new ReminderResource(
            entity.getId(),
            entity.getProfileId(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getNotificationChannel().name(),
            entity.getRemindAt().toString(),
            entity.isActive(),
            entity.isRecurring(),
            entity.getRecurrencePattern()
        );
    }
}
