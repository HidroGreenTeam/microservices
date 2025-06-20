package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CreateStandaloneActivityCommand;

/**
 * Standalone activity command service interface 🆓
 */
public interface StandaloneActivityCommandService {

    /**
     * Create a standalone activity
     *
     * @param command the create standalone activity command
     * @return the created activity ID
     */
    Long handle(CreateStandaloneActivityCommand command);
}
