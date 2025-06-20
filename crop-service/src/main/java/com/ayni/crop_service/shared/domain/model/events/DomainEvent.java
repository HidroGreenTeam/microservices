package com.ayni.crop_service.shared.domain.model.events;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events
 */
public interface DomainEvent {
    String getEventId();
    LocalDateTime getOccurredOn();
    
   
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
