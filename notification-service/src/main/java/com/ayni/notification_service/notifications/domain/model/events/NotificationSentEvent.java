package com.ayni.notification_service.notifications.domain.model.events;
 
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * NotificationSentEvent domain event
 */
@Getter
public final class NotificationSentEvent extends ApplicationEvent {
    private final Long notificationId;
    private final Long userId;

    public NotificationSentEvent(Object source, Long notificationId, Long userId) {
        super(source);
        this.notificationId = notificationId;
        this.userId = userId;
    }
    
    // Getter para compatibilidad (deprecated)
    @Deprecated
    public Long getFarmerId() {
        return userId; // Retorna userId para compatibilidad
    }
}
