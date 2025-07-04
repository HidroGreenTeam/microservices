package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByProfileIdQuery;

import java.util.List;
import java.util.Optional;


public interface NotificationQueryService {
    List<Notification> handle(GetNotificationsByProfileIdQuery query);
    List<Notification> handle(GetAllNotificationsQuery query);
    Optional<Notification> handle(GetNotificationByIdQuery query);
}
