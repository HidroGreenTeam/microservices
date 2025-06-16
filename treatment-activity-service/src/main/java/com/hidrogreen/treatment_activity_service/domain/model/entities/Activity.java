package com.hidrogreen.treatment_activity_service.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.ActivityStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "scheduled_date_time", nullable = false)
    private LocalDateTime scheduledDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.PENDING;
    
    @Column(name = "reminder_minutes_before")
    private Integer reminderMinutesBefore = 30; // Por defecto 30 minutos antes
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "notification_sent")
    private Boolean notificationSent = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
      @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "treatment_id", nullable = false, insertable = false, updatable = false)
    private Long treatmentId;
      @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    @JsonBackReference
    private Treatment treatment;
    
    public Activity(String name, String description, LocalDateTime scheduledDateTime, Integer reminderMinutesBefore) {
        this.name = name;
        this.description = description;
        this.scheduledDateTime = scheduledDateTime;
        this.reminderMinutesBefore = reminderMinutesBefore != null ? reminderMinutesBefore : 30;
        this.status = ActivityStatus.PENDING;
        this.notificationSent = false;
    }
    
    public void complete() {
        this.status = ActivityStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = ActivityStatus.CANCELLED;
    }
    
    public void markInProgress() {
        this.status = ActivityStatus.IN_PROGRESS;
    }
    
    public void markOverdue() {
        this.status = ActivityStatus.OVERDUE;
    }
    
    public void markNotificationSent() {
        this.notificationSent = true;
    }
    
    public LocalDateTime getNotificationTime() {
        return scheduledDateTime.minusMinutes(reminderMinutesBefore);
    }
    
    public boolean shouldSendNotification() {
        return !notificationSent && 
               status == ActivityStatus.PENDING && 
               LocalDateTime.now().isAfter(getNotificationTime());
    }
}
