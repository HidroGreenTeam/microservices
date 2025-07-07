package com.hidrogreen.treatment_service.treatment.domain.model.entities;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStepStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Treatment step entity
 */
@Entity
@Table(name = "treatment_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreatmentStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Embedded
    private TreatmentStepStatus status;

    @Column(name = "has_reminder")
    private boolean hasReminder;

    @Column(name = "reminder_minutes_before")
    private Integer reminderMinutesBefore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TreatmentStep(Treatment treatment, String name, String description, 
                        LocalDateTime scheduledDate, boolean hasReminder, 
                        Integer reminderMinutesBefore) {
        this.treatment = treatment;
        this.name = name;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.hasReminder = hasReminder;
        this.reminderMinutesBefore = reminderMinutesBefore;
        this.status = new TreatmentStepStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void start() {
        if (!status.isPending()) {
            throw new IllegalStateException("Step can only be started when pending");
        }
        this.status = new TreatmentStepStatus(TreatmentStepStatus.Status.IN_PROGRESS);
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (!status.isInProgress()) {
            throw new IllegalStateException("Step can only be completed when in progress");
        }
        this.status = new TreatmentStepStatus(TreatmentStepStatus.Status.COMPLETED);
        this.completedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void skip() {
        if (!status.isPending()) {
            throw new IllegalStateException("Step can only be skipped when pending");
        }
        this.status = new TreatmentStepStatus(TreatmentStepStatus.Status.SKIPPED);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateScheduledDate(LocalDateTime newScheduledDate) {
        this.scheduledDate = newScheduledDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateReminder(boolean hasReminder, Integer reminderMinutesBefore) {
        this.hasReminder = hasReminder;
        this.reminderMinutesBefore = reminderMinutesBefore;
        this.updatedAt = LocalDateTime.now();
    }
}
