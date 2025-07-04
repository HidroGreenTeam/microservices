package com.ayni.notification_service.notifications.domain.model.queries;


public record GetNotificationsByProfileIdQuery(Long profileId) {
    public GetNotificationsByProfileIdQuery {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be a positive number");
        }
    }
}
