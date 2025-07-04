package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


@Embeddable
public record ActivityFrequency(@Enumerated(EnumType.STRING) Frequency frequency) {
    public ActivityFrequency() {
        this(Frequency.ONCE);
    }

    public ActivityFrequency {
        if (frequency == null) {
            throw new IllegalArgumentException("Activity frequency cannot be null");
        }
    }

    public boolean isOnce() {
        return frequency == Frequency.ONCE;
    }

    public boolean isDaily() {
        return frequency == Frequency.DAILY;
    }

    public boolean isWeekly() {
        return frequency == Frequency.WEEKLY;
    }

    public boolean isMonthly() {
        return frequency == Frequency.MONTHLY;
    }

    public boolean isRecurring() {
        return frequency != Frequency.ONCE;
    }

    public int getDaysInterval() {
        return switch (frequency) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
            default -> 0;
        };
    }

    public enum Frequency {
        ONCE, DAILY, WEEKLY, MONTHLY
    }
}
