package com.ayni.notification_service.notifications.domain.model.events;

import com.ayni.notification_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ActivityDueEvent extends DomainEvent {
    
    private final Long activityId;
    private final Long profileId;
    private final String activityTitle;
    private final LocalDateTime dueDate;
    
    public ActivityDueEvent(Object source, Long activityId, Long profileId, 
                          String activityTitle, LocalDateTime dueDate) {
        super(source);
        this.activityId = activityId;
        this.profileId = profileId;
        this.activityTitle = activityTitle;
        this.dueDate = dueDate;
    }
    
    
    public Long getActivityId() { return activityId; }
    public Long getProfileId() { return profileId; }
    public String getActivityTitle() { return activityTitle; }
    public LocalDateTime getDueDate() { return dueDate; }
} 