package com.hidrogreen.treatment_service.treatment.domain.model.queries;

import java.time.LocalDate;

/**
 * Query to get today's activities
 */
public record GetTodaysActivitiesQuery(
    LocalDate date,
    Long cropId
) {
    public GetTodaysActivitiesQuery {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (cropId != null && cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be negative");
        }
    }

    public GetTodaysActivitiesQuery(Long cropId) {
        this(LocalDate.now(), cropId);
    }

    public GetTodaysActivitiesQuery() {
        this(LocalDate.now(), null);
    }
}
