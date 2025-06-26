package com.ayni.notification_service.notifications.interfaces.rest.transform;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.interfaces.rest.resources.NotificationResource;

/**
 * NotificationResourceFromEntityAssembler
 */
public class NotificationResourceFromEntityAssembler {
    
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
            entity.getId(),
            entity.getfarmerId(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getNotificationType().name(),
            entity.getNotificationChannel().name(),
            entity.getNotificationStatus().name(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null,
            entity.getSentAt() != null ? entity.getSentAt().toString() : null
        );
    }
}
