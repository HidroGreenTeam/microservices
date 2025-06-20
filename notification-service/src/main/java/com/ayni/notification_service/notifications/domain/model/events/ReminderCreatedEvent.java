package com.ayni.notification_service.notifications.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * ReminderCreatedEvent domain event
 */
@Getter
public final class ReminderCreatedEvent extends ApplicationEvent {
    private final Long reminderId;
    private final Long profileId;
    private final LocalDateTime remindAt;

    public ReminderCreatedEvent(Object source, Long reminderId, Long profileId, LocalDateTime remindAt) {
        super(source);
        this.reminderId = reminderId;
        this.profileId = profileId;
        this.remindAt = remindAt;
    }
}
