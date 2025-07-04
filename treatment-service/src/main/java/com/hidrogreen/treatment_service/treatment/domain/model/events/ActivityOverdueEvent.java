package com.hidrogreen.treatment_service.treatment.domain.model.events;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public final class ActivityOverdueEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long activityId;
    private final Long cropId;
    private final String title;
    private final String activityType;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime dueDate;
    private final String origin;

    public ActivityOverdueEvent(Activity activity) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.activityId = activity.getActivityId();
        this.cropId = activity.getCropId();
        this.title = activity.getTitle();
        this.activityType = activity.getActivityType().type().name();
        this.scheduledAt = activity.getScheduledAt();
        this.dueDate = activity.getDueDate();
        this.origin = activity.getOrigin().origin().name();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "ActivityOverdueEvent";
    }
}
