package com.hidrogreen.treatment_service.shared.domain.model.events;

import java.time.LocalDateTime;


public interface DomainEvent {
    String getEventId();
    LocalDateTime getOccurredOn();
    
   
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
