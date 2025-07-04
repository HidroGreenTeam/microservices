package com.hidrogreen.treatment_service.treatment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


@Embeddable
public record ActivityOrigin(@Enumerated(EnumType.STRING) Origin origin) {
    public ActivityOrigin() {
        this(Origin.STANDALONE);
    }

    public ActivityOrigin {
        if (origin == null) {
            throw new IllegalArgumentException("Activity origin cannot be null");
        }
    }

    public boolean isStandalone() {
        return origin == Origin.STANDALONE;
    }

    public boolean isTreatmentBased() {
        return origin == Origin.TREATMENT_BASED;
    }

    public enum Origin {
        STANDALONE, TREATMENT_BASED
    }
}
