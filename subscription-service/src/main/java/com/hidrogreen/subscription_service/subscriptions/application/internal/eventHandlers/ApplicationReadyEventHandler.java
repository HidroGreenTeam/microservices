package com.hidrogreen.subscription_service.subscriptions.application.internal.eventHandlers;

import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ApplicationReadyEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public ApplicationReadyEventHandler(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) throws SQLException {
        var applicationName = event.getApplicationContext().getId();
        LOGGER.info("Starting to verify if subscription plans seeding is needed for application {}", applicationName);
        
        var subscriptionTypes = SubscriptionType.values();
        
        for (SubscriptionType type : subscriptionTypes) {
            var existsSubscriptionPlan = subscriptionPlanRepository.existsByPlanType(type);
            LOGGER.info("Checking subscription plan for type: {}", type.name());
            
            if (!existsSubscriptionPlan) {
                LOGGER.info("Subscription plan for type {} not exists. Proceeding to seed it", type.name());
                var subscriptionPlan = new SubscriptionPlan(type);
                subscriptionPlanRepository.save(subscriptionPlan);
                LOGGER.info("Subscription plan for type {} seeded", type.name());
            }
        }
        
        LOGGER.info("Subscription plans seeding verification finished for application {}", applicationName);
    }
}
