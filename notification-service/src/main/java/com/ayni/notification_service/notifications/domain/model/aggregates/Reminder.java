package com.ayni.notification_service.notifications.domain.model.aggregates;

import com.ayni.notification_service.notifications.domain.model.events.ReminderCreatedEvent;
import com.ayni.notification_service.notifications.domain.model.valueobjects.*;
import com.ayni.notification_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Reminder aggregate root
 */
@Entity
@Getter
@Setter
public class Reminder extends AuditableAbstractAggregateRoot<Reminder> {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "crop_id")
    private Long cropId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false)
    private NotificationChannel notificationChannel;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "remind_at", nullable = false)
    private LocalDateTime remindAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY

    protected Reminder() {}    public Reminder(Long profileId, NotificationChannel notificationChannel, 
                   String title, String message, LocalDateTime remindAt) {
        this.profileId = profileId;
        this.notificationChannel = notificationChannel;
        this.title = title;
        this.message = message;
        this.remindAt = remindAt;
        this.isActive = true;
        this.isRecurring = false;
    }
    
    /**
     * Publishes the ReminderCreatedEvent after the reminder is persisted
     */
    public void publishCreatedEvent() {
        this.registerEvent(new ReminderCreatedEvent(this, getId(), this.profileId, this.remindAt));
    }

    public Reminder(Long profileId, Long activityId, NotificationChannel notificationChannel, 
                   String title, String message, LocalDateTime remindAt) {
        this(profileId, notificationChannel, title, message, remindAt);
        this.activityId = activityId;
    } 

    public Reminder(Long profileId, Long cropId, NotificationChannel notificationChannel, 
                   String title, String message, LocalDateTime remindAt, boolean isForCrop) {
        this(profileId, notificationChannel, title, message, remindAt);
        this.cropId = cropId;
    }

    public void setRecurring(String recurrencePattern) {
        this.isRecurring = true;
        this.recurrencePattern = recurrencePattern;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isDue() {
        return this.isActive && this.remindAt.isBefore(LocalDateTime.now());
    }

    public void updateRemindTime(LocalDateTime newRemindAt) {
        this.remindAt = newRemindAt;
    }
    
    // Additional getters for fields not covered by Lombok
    public Long getProfileId() { return profileId; }
    public Long getActivityId() { return activityId; }
    public Long getCropId() { return cropId; }
    public NotificationChannel getNotificationChannel() { return notificationChannel; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getRemindAt() { return remindAt; }
    public boolean isActive() { return isActive; }
    public boolean isRecurring() { return isRecurring; }
    public String getRecurrencePattern() { return recurrencePattern; }
}
