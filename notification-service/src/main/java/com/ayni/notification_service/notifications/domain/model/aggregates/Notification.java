package com.ayni.notification_service.notifications.domain.model.aggregates;    

import com.ayni.notification_service.notifications.domain.model.events.NotificationSentEvent;
import com.ayni.notification_service.notifications.domain.model.valueobjects.*;
import com.ayni.notification_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Notification aggregate root
 */
@Entity
@Getter
@Setter
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_role", nullable = false)
    private UserRole recipientRole;

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

    public Notification(Long userId, UserRole recipientRole, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message) {
        this.userId = userId;
        this.recipientRole = recipientRole;
        this.notificationType = notificationType;
        this.notificationChannel = notificationChannel;
        this.notificationStatus = NotificationStatus.PENDING;
        this.title = title;
        this.message = message;
    }

    public Notification(Long userId, UserRole recipientRole, Long activityId, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message) {
        this(userId, recipientRole, notificationType, notificationChannel, title, message);
        this.activityId = activityId;
    }

    public Notification(Long userId, UserRole recipientRole, Long cropId, NotificationType notificationType, 
                       NotificationChannel notificationChannel, String title, String message, boolean isForCrop) {
        this(userId, recipientRole, notificationType, notificationChannel, title, message);
        this.cropId = cropId;
    }
    
    /**
     * Publishes the NotificationSentEvent after the notification is persisted
     */
    public void publishSentEvent() {
        this.registerEvent(new NotificationSentEvent(this, getId(), this.userId));
    }

    public void scheduleFor(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void markAsSent() {
        this.notificationStatus = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        
        // The event should be published separately using publishSentEvent() method
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
    
    // Custom getter method for consistency with existing code (deprecated)
    @Deprecated
    public Long getfarmerId() { 
        return userId; // Retorna userId para mantener compatibilidad
    }
    
    // Getters para la nueva estructura
    public Long getUserId() {
        return userId;
    }
    
    public UserRole getRecipientRole() {
        return recipientRole;
    }
}
