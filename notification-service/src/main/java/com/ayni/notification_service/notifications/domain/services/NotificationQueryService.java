package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByUserIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByfarmerIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * NotificationQueryService
 */
public interface NotificationQueryService {
    List<Notification> handle(GetNotificationsByUserIdQuery query);
    List<Notification> handle(GetAllNotificationsQuery query);
    Optional<Notification> handle(GetNotificationByIdQuery query);
    
    // Para compatibilidad (deprecated)
    @Deprecated
    List<Notification> handle(GetNotificationsByfarmerIdQuery query);
}
