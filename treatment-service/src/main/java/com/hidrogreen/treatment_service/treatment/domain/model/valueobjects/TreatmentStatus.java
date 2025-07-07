package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Treatment status value object
 */
@Embeddable
public record TreatmentStatus(@Enumerated(EnumType.STRING) Status status) {
    
    public TreatmentStatus {
        if (status == null) {
            throw new IllegalArgumentException("Treatment status cannot be null");
        }
    }

    public TreatmentStatus() {
        this(Status.PENDING);
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isInProgress() {
        return status == Status.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public enum Status {
        PENDING,      // Treatment created but not started
        IN_PROGRESS,  // Treatment is active with steps being executed
        COMPLETED,    // All steps completed
        CANCELLED    // Treatment cancelled before completion
    }
}
