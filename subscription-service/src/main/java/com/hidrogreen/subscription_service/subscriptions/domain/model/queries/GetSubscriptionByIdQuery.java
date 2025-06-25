package com.hidrogreen.subscription_service.subscriptions.domain.model.queries;

public record GetSubscriptionByIdQuery(Long subscriptionId) {
    public GetSubscriptionByIdQuery {
        if (subscriptionId == null || subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID is required and must be positive");
        }
    }
}
