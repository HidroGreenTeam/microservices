package com.hidrogreen.treatment_service.treatment.domain.model.events;

import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;

import java.time.LocalDateTime;

/**
 * Treatment Created Event
 */
public class TreatmentCreatedEvent implements DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Treatment treatment;

    public TreatmentCreatedEvent(Treatment treatment) {
        this.treatment = treatment;
        this.eventId = "treatment.created." + treatment.getId();
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Treatment getTreatment() {
        return treatment;
    }
}
