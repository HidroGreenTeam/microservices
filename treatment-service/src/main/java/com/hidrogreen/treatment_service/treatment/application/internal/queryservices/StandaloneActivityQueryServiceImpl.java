package com.hidrogreen.treatment_service.treatment.application.internal.queryservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.GetStandaloneActivitiesQuery;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.StandaloneActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Standalone activity query service implementation 🆓
 */
@Service
@AllArgsConstructor
public class StandaloneActivityQueryServiceImpl {

    private final StandaloneActivityRepository standaloneActivityRepository;

    public List<StandaloneActivity> handle(GetStandaloneActivitiesQuery query) {
        if (query.getCropId() != null && query.createdByUser() != null) {
            return standaloneActivityRepository.findByCropIdAndCreatedByUser(
                query.getCropId(), query.createdByUser());
        } else if (query.getCropId() != null) {
            return standaloneActivityRepository.findByCropId(query.getCropId());
        } else if (query.createdByUser() != null) {
            return standaloneActivityRepository.findByCreatedByUser(query.createdByUser());
        } else {
            return standaloneActivityRepository.findAll();
        }
    }

    public List<StandaloneActivity> getActivitiesWithReminders() {
        return standaloneActivityRepository.findByReminderEnabledTrue();
    }

    public List<StandaloneActivity> getActivitiesByFrequency(String frequency) {
        return standaloneActivityRepository.findByFrequency(frequency);
    }
}
