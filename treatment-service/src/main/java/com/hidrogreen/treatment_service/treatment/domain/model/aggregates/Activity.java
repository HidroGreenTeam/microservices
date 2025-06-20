package com.hidrogreen.treatment_service.treatment.domain.model.aggregates;

import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.*;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.ActivityStep;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.ActivityNote;
import com.hidrogreen.treatment_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Base Activity aggregate root - Abstract class
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Activity extends AuditableAbstractAggregateRoot<Activity> {    @Column(name = "crop_id")
    protected Long cropId;
  
    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "description", length = 1000)
    protected String description;

    @Embedded
    protected ActivityType activityType;

    @Embedded
    protected ActivityOrigin origin;

    @Embedded
    protected ActivityStatus status;

    @Embedded
    protected ActivityFrequency frequency;

    @Column(name = "scheduled_at", nullable = false)
    protected LocalDateTime scheduledAt;

    @Column(name = "completed_at")
    protected LocalDateTime completedAt;

    @Column(name = "due_date")
    protected LocalDateTime dueDate;

    @Column(name = "priority", nullable = false)
    protected int priority = 1;

    @Column(name = "instructions", length = 2000)
    protected String instructions;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ActivityStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ActivityNote> notes = new ArrayList<>();

    protected Activity() {}    protected Activity(Long cropId, String title, String description, 
                      ActivityType activityType, ActivityOrigin origin, 
                      LocalDateTime scheduledAt, ActivityFrequency frequency) {
        this.cropId = cropId;
        this.title = title;
        this.description = description;
        this.activityType = activityType;
        this.origin = origin;
        this.scheduledAt = scheduledAt;
        this.frequency = frequency;
        this.status = new ActivityStatus(ActivityStatus.Status.PENDING);
        this.calculateDueDate();
    }

    public void complete() {
        if (this.status.isCompleted()) {
            throw new IllegalStateException("Activity is already completed");
        }
        this.status = new ActivityStatus(ActivityStatus.Status.COMPLETED);
        this.completedAt = LocalDateTime.now();
    }

    public void markAsOverdue() {
        if (!this.status.isPending()) {
            return;
        }
        this.status = new ActivityStatus(ActivityStatus.Status.OVERDUE);
    }

    public void cancel() {
        if (this.status.isCompleted()) {
            throw new IllegalStateException("Cannot cancel a completed activity");
        }
        this.status = new ActivityStatus(ActivityStatus.Status.CANCELLED);
    }

    public void updateInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void addStep(ActivityStep step) {
        this.steps.add(step);
    }

    public void addNote(ActivityNote note) {
        this.notes.add(note);
    }

    public void updatePriority(int priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        this.priority = priority;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status.isPending();
    }

    protected void calculateDueDate() {
        if (scheduledAt != null) {
            // Default: due 24 hours after scheduled time
            this.dueDate = scheduledAt.plusHours(24);
        }
    }    public Long getActivityId() {
        return this.getId();
    }

    public Long getCropId() {
        return cropId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public ActivityOrigin getOrigin() {
        return origin;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public ActivityFrequency getFrequency() {
        return frequency;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<ActivityStep> getSteps() {
        return steps;
    }

    public List<ActivityNote> getNotes() {
        return notes;
    }
}
