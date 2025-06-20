package com.ayni.notification_service.shared.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent extends ApplicationEvent {
    
    private final LocalDateTime occurredOn;
    
    protected DomainEvent(Object source) {
        super(source);
        this.occurredOn = LocalDateTime.now();
    }
} 