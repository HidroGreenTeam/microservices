package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.SubscriptionResource;

public class SubscriptionResourceFromEntityAssembler {
    public static SubscriptionResource toResourceFromEntity(Subscription entity) {
        return new SubscriptionResource(
            entity.getId(),
            entity.getUserId(),
            entity.getSubscriptionPlan().getPlanType(),
            entity.getSubscriptionPlan().getName(),
            entity.getStatus(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getAutoRenew(),
            entity.getPaymentReference(),
            entity.getDaysRemaining(),
            entity.isActive(),
            entity.isExpired(),
            entity.getCancellationReason(),
            entity.getCancelledAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
