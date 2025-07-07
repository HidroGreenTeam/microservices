package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Treatment frequency value object - defines how often a treatment should be repeated
 */
@Embeddable
public record TreatmentFrequency(
    @Enumerated(EnumType.STRING) 
    Frequency frequency, 
    
    @Column(name = "interval_value")
    Integer interval) {
    
    public TreatmentFrequency {
        if (frequency == null) {
            throw new IllegalArgumentException("Treatment frequency cannot be null");
        }
        if (interval != null && interval <= 0) {
            throw new IllegalArgumentException("Treatment interval must be positive");
        }
    }

    public TreatmentFrequency() {
        this(Frequency.ONCE, null);
    }

    public boolean isRecurring() {
        return frequency != Frequency.ONCE;
    }

    public enum Frequency {
        ONCE,       // Treatment happens only once
        DAILY,      // Treatment repeats every X days
        WEEKLY,     // Treatment repeats every X weeks
        MONTHLY     // Treatment repeats every X months
    }
}
