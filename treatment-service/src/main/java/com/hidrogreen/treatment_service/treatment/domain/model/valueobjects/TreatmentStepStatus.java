package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Treatment step status value object
 */
@Embeddable
public record TreatmentStepStatus(@Enumerated(EnumType.STRING) Status status) {
    
    public TreatmentStepStatus {
        if (status == null) {
            throw new IllegalArgumentException("Treatment step status cannot be null");
        }
    }

    public TreatmentStepStatus() {
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

    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }

    public enum Status {
        PENDING,      // Step is waiting to be started
        IN_PROGRESS,  // Step is currently being executed
        COMPLETED,    // Step has been completed
        SKIPPED      // Step was skipped (optional steps only)
    }
}
