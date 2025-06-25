package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform;

import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.SubscriptionPlanResource;

public class SubscriptionPlanResourceFromEntityAssembler {
    public static SubscriptionPlanResource toResourceFromEntity(SubscriptionPlan entity) {
        return new SubscriptionPlanResource(
            entity.getId(),
            entity.getPlanType(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getDurationDays(),
            entity.getMaxCrops(),
            entity.getMaxReports(),
            entity.getHasPrioritySupport(),
            entity.getHasAdvancedAnalytics(),
            entity.getIsActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
