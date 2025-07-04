package com.ayni.notification_service.notifications.domain.model.events;

import com.ayni.notification_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ActivityOverdueEvent extends DomainEvent {
    
    private final Long activityId;
    private final Long cropId;
    private final String title;
    private final String activityType;
    private final LocalDateTime dueDate;
    
    public ActivityOverdueEvent(Object source, Long activityId, Long cropId, 
                              String title, String activityType, LocalDateTime dueDate) {
        super(source);
        this.activityId = activityId;
        this.cropId = cropId;
        this.title = title;
        this.activityType = activityType;
        this.dueDate = dueDate;
    }
    
    
    public Long getActivityId() { return activityId; }
    public Long getCropId() { return cropId; }
    public String getTitle() { return title; }
    public String getActivityType() { return activityType; }
    public LocalDateTime getDueDate() { return dueDate; }
} 