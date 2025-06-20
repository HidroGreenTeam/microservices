package com.ayni.notification_service.notifications.domain.model.events;
 
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * NotificationSentEvent domain event
 */
@Getter
public final class NotificationSentEvent extends ApplicationEvent {
    private final Long notificationId;
    private final Long profileId;

    public NotificationSentEvent(Object source, Long notificationId, Long profileId) {
        super(source);
        this.notificationId = notificationId;
        this.profileId = profileId;
    }
}
