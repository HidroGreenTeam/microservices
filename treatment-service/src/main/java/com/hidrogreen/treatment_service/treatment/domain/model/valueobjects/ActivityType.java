package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Activity type value object
 */
@Embeddable
public record ActivityType(@Enumerated(EnumType.STRING) Type type) {
    public ActivityType() {
        this(Type.GENERAL);
    }

    public ActivityType {
        if (type == null) {
            throw new IllegalArgumentException("Activity type cannot be null");
        }
    }

    public boolean isWatering() {
        return type == Type.WATERING;
    }

    public boolean isFertilizing() {
        return type == Type.FERTILIZING;
    }

    public boolean isPruning() {
        return type == Type.PRUNING;
    }

    public boolean isSpraying() {
        return type == Type.SPRAYING;
    }

    public boolean isPestControl() {
        return type == Type.PEST_CONTROL;
    }

    public enum Type {
        WATERING, FERTILIZING, PRUNING, SPRAYING, PEST_CONTROL, HARVESTING, SOIL_PREPARATION, GENERAL
    }
}
