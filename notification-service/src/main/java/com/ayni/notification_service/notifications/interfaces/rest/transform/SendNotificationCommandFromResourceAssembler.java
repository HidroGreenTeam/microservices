package com.ayni.notification_service.notifications.interfaces.rest.transform;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.interfaces.rest.resources.SendNotificationResource;

/**
 * SendNotificationCommandFromResourceAssembler
 */
public class SendNotificationCommandFromResourceAssembler {
    
    public static SendNotificationCommand toCommandFromResource(SendNotificationResource resource) {
        return new SendNotificationCommand(
            resource.profileId(),
            NotificationType.valueOf(resource.notificationType().toUpperCase()),
            NotificationChannel.valueOf(resource.notificationChannel().toUpperCase()),
            resource.title(),
            resource.message(),
            resource.activityId(),
            resource.cropId()
        );
    }
}
