package com.hidrogreen.user_service.profiles.application.internal.eventHandlers.eventListeners;

import com.hidrogreen.user_service.iam.domain.model.events.UserRegisteredEvent;

import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.events.FarmerCreatedEvent;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserRegisteredEventHandler {

    private final FarmerCommandService farmerCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public UserRegisteredEventHandler(FarmerCommandService farmerCommandService, ApplicationEventPublisher eventPublisher) {
        this.farmerCommandService = farmerCommandService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            CreateFarmerCommand createFarmerCommand = new CreateFarmerCommand(event.getFullName(), event.getEmail(), "", event.getPassword());
            Long farmerId = farmerCommandService.createFarmer(createFarmerCommand);

            FarmerCreatedEvent farmerCreatedEvent = new FarmerCreatedEvent(this, farmerId, event.getEmail());
            eventPublisher.publishEvent(farmerCreatedEvent);

        } catch (IllegalArgumentException e) {
            System.out.println("Farmer with email " + event.getEmail() + " already exists. Skipping creation.");
        }
    }
}


