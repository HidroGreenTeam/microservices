package com.ayni.notification_service.notifications.domain.model.aggregates;    

import com.ayni.notification_service.notifications.domain.model.events.NotificationSentEvent;
import com.ayni.notification_service.notifications.domain.model.valueobjects.*;
import com.ayni.notification_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "crop_id")
    private Long cropId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false)
    private NotificationChannel notificationChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status", nullable = false)
    private NotificationStatus notificationStatus;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    protected Notification() {}

    public Notification(Long profileId, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message) {
        this.profileId = profileId;
        this.notificationType = notificationType;
        this.notificationChannel = notificationChannel;
        this.notificationStatus = NotificationStatus.PENDING;
        this.title = title;
        this.message = message;
    }    public Notification(Long profileId, Long activityId, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message) {
        this(profileId, notificationType, notificationChannel, title, message);
        this.activityId = activityId;
    }

    public Notification(Long profileId, Long cropId, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message, boolean isForCrop) {
        this(profileId, notificationType, notificationChannel, title, message);
        this.cropId = cropId;
    }
    
    
    public void publishSentEvent() {
        this.registerEvent(new NotificationSentEvent(this, getId(), this.profileId));
    }

    public void scheduleFor(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }    public void markAsSent() {
        this.notificationStatus = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        
        
    }

    public void markAsDelivered() {
        this.notificationStatus = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.notificationStatus == NotificationStatus.PENDING;
    }

    public boolean isScheduled() {
        return this.scheduledAt != null && this.scheduledAt.isAfter(LocalDateTime.now());
    }
    
    
    public Long getProfileId() { return profileId; }
    public Long getActivityId() { return activityId; }
    public Long getCropId() { return cropId; }
    public NotificationType getNotificationType() { return notificationType; }
    public NotificationChannel getNotificationChannel() { return notificationChannel; }
    public NotificationStatus getNotificationStatus() { return notificationStatus; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
}
