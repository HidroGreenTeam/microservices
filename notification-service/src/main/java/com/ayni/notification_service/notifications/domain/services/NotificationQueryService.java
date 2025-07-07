package com.ayni.notification_service.notifications.domain.services;

import java.util.List;
import java.util.Optional;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByProfileIdQuery;

/**
 * NotificationQueryService
 */
public interface NotificationQueryService {
    List<Notification> handle(GetNotificationsByProfileIdQuery query);
    Optional<Notification> handle(GetNotificationByIdQuery query);
}
