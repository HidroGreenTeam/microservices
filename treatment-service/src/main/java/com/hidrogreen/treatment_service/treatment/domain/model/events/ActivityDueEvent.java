package com.hidrogreen.treatment_service.treatment.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;


@Getter
public final class ActivityDueEvent extends ApplicationEvent {
    private final Long activityId;
    private final Long profileId;
    private final String activityTitle;
    private final LocalDateTime dueDate;
    private final Long cropId;

    public ActivityDueEvent(Object source, Long activityId, Long profileId, 
                           String activityTitle, LocalDateTime dueDate, Long cropId) {
        super(source);
        this.activityId = activityId;
        this.profileId = profileId;
        this.activityTitle = activityTitle;
        this.dueDate = dueDate;
        this.cropId = cropId;
    }
}
