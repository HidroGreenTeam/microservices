package com.ayni.notification_service.notifications.domain.model.aggregates;    

import java.time.LocalDateTime;

import com.ayni.notification_service.notifications.domain.model.events.NotificationSentEvent;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationStatus;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Notification aggregate root
 */
@Entity
@Getter
@Setter
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

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
    }    

    public void publishSentEvent() {
        this.registerEvent(new NotificationSentEvent(this, getId(), this.profileId));
    }

    public void scheduleFor(LocalDateTime scheduledAt) {
        if (scheduledAt != null && scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule notification for past time");
        }
        this.scheduledAt = scheduledAt;
    }

    public void markAsSent() {
        if (!isPending()) {
            throw new IllegalStateException("Can only mark pending notifications as sent");
        }
        this.notificationStatus = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
         this.publishSentEvent();
    }

    public void markAsDelivered() {
        if (!hasBeenSent()) {
            throw new IllegalStateException("Can only mark sent notifications as delivered");
        }
        this.notificationStatus = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }


    public boolean isPending() {
        return this.notificationStatus == NotificationStatus.PENDING;
    }

    public boolean isScheduled() {
        return this.scheduledAt != null && this.scheduledAt.isAfter(LocalDateTime.now());
    }

    public boolean isReadyToSend() {
        return isPending() && (scheduledAt == null || !isScheduled());
    }

    public boolean hasBeenSent() {
        return this.sentAt != null;
    }

    public boolean hasBeenDelivered() {
        return this.deliveredAt != null;
    }
}
