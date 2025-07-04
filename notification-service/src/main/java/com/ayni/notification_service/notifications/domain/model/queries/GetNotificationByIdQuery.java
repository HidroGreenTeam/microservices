package com.ayni.notification_service.notifications.domain.model.queries;


public record GetNotificationByIdQuery(Long notificationId) {
    public GetNotificationByIdQuery {
        if (notificationId == null || notificationId <= 0) {
            throw new IllegalArgumentException("Notification ID cannot be null or negative");
        }
    }
}
