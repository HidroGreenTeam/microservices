package com.hidrogreen.treatment_service.treatment.domain.model.events;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;


@Getter
public final class ActivityCompletedEvent implements DomainEvent {
    private final String eventId;
    private final java.time.LocalDateTime occurredOn;
    private final Long activityId;
    private final Long cropId;
    private final String title;
    private final String activityType;
    private final String origin;
    private final Long treatmentId;

    public ActivityCompletedEvent(Activity activity) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = java.time.LocalDateTime.now();
        this.activityId = activity.getActivityId();
        this.cropId = activity.getCropId();
        this.title = activity.getTitle();
        this.activityType = activity.getActivityType().type().name();
        this.origin = activity.getOrigin().origin().name();
        
        
        if (activity instanceof com.hidrogreen.treatment_service.treatment.domain.model.aggregates.TreatmentActivity treatmentActivity) {
            this.treatmentId = treatmentActivity.getTreatmentId();
        } else {
            this.treatmentId = null;
        }
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
        return "ActivityCompletedEvent";
    }
}
