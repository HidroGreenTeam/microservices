package com.ayni.notification_service.notifications.interfaces.rest.transform;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.interfaces.rest.resources.NotificationHistoryResource;


public class NotificationHistoryResourceFromEntityAssembler {
    
    public static NotificationHistoryResource toResourceFromEntity(Notification entity) {
        return new NotificationHistoryResource(
            entity.getId(),
            entity.getProfileId(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getNotificationType().name(),
            entity.getNotificationChannel().name(),
            entity.getNotificationStatus().name(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null,
            entity.getSentAt() != null ? entity.getSentAt().toString() : null,
            entity.getDeliveredAt() != null ? entity.getDeliveredAt().toString() : null,
            entity.getActivityId(),
            entity.getCropId()
        );
    }
}
