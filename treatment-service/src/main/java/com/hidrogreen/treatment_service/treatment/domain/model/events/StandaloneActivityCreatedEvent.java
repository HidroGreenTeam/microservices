package com.hidrogreen.treatment_service.treatment.domain.model.events;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;


@Getter
public final class StandaloneActivityCreatedEvent implements DomainEvent {
    private final String eventId;
    private final java.time.LocalDateTime occurredOn;
    private final Long activityId;
    private final Long cropId;
    private final String title;
    private final String activityType;
    private final String createdByUser;

    public StandaloneActivityCreatedEvent(StandaloneActivity activity) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = java.time.LocalDateTime.now();
        this.activityId = activity.getActivityId();
        this.cropId = activity.getCropId();
        this.title = activity.getTitle();
        this.activityType = activity.getActivityType().type().name();
        this.createdByUser = activity.getCreatedByUser();
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
        return "StandaloneActivityCreatedEvent";
    }
}
