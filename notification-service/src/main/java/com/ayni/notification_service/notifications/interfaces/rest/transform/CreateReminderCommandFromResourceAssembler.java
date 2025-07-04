package com.ayni.notification_service.notifications.interfaces.rest.transform;

import com.ayni.notification_service.notifications.domain.model.commands.ScheduleReminderCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.interfaces.rest.resources.CreateReminderResource;

import java.time.LocalDateTime;


public class CreateReminderCommandFromResourceAssembler {
    
    public static ScheduleReminderCommand toCommandFromResource(CreateReminderResource resource) {
        return new ScheduleReminderCommand(
            resource.profileId(),
            NotificationChannel.valueOf(resource.notificationChannel().toUpperCase()),
            resource.title(),
            resource.message(),
            LocalDateTime.parse(resource.remindAt()),
            resource.activityId(),
            resource.cropId(),
            resource.isRecurring(),
            resource.recurrencePattern()
        );
    }
}
