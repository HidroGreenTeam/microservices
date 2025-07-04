package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.commands.CreateStandaloneActivityCommand;


public interface StandaloneActivityCommandService {

    
    Long handle(CreateStandaloneActivityCommand command);
}
