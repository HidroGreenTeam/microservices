package com.ayni.notification_service.notifications.domain.model.queries;

import com.ayni.notification_service.notifications.domain.model.valueobjects.UserRole;

/**
 * GetNotificationsByUserIdQuery
 */
public record GetNotificationsByUserIdQuery(Long userId, UserRole recipientRole) {
    public GetNotificationsByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
    
    // Constructor para buscar por userId sin filtrar por rol
    public GetNotificationsByUserIdQuery(Long userId) {
        this(userId, null);
    }
}

/**
 * @deprecated Use GetNotificationsByUserIdQuery instead
 */
@Deprecated
record GetNotificationsByfarmerIdQuery(Long farmerId) {
    public GetNotificationsByfarmerIdQuery {
        if (farmerId == null || farmerId <= 0) {
            throw new IllegalArgumentException("Profile ID must be a positive number");
        }
    }
}
