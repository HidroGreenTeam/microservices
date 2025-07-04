package com.hidrogreen.treatment_service.treatment.domain.model.events;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.TreatmentActivity;
import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;


@Getter
public final class TreatmentActivityCreatedEvent implements DomainEvent {
    private final String eventId;
    private final java.time.LocalDateTime occurredOn;
    private final Long activityId;
    private final Long cropId;
    private final Long treatmentId;
    private final String title;
    private final String activityType;
    private final Integer treatmentStepOrder;
    private final boolean isMandatory;

    public TreatmentActivityCreatedEvent(TreatmentActivity activity) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = java.time.LocalDateTime.now();
        this.activityId = activity.getActivityId();
        this.cropId = activity.getCropId();
        this.treatmentId = activity.getTreatmentId();
        this.title = activity.getTitle();
        this.activityType = activity.getActivityType().type().name();
        this.treatmentStepOrder = activity.getTreatmentStepOrder();
        this.isMandatory = activity.isMandatory();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public java.time.LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "TreatmentActivityCreatedEvent";
    }
}
