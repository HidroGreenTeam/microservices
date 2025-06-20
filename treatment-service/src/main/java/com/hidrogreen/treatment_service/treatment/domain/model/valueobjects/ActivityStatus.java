package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Activity status value object
 */
@Embeddable
public record ActivityStatus(@Enumerated(EnumType.STRING) Status status) {
    public ActivityStatus() {
        this(Status.PENDING);
    }

    public ActivityStatus {
        if (status == null) {
            throw new IllegalArgumentException("Activity status cannot be null");
        }
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isOverdue() {
        return status == Status.OVERDUE;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public enum Status {
        PENDING, COMPLETED, OVERDUE, CANCELLED
    }
}
