package com.hidrogreen.subscription_service.subscriptions.domain.model.commands;

import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;

public record RenewSubscriptionCommand(
    Long subscriptionId,
    SubscriptionType newSubscriptionType,
    String paymentReference
) {
    public RenewSubscriptionCommand {
        if (subscriptionId == null || subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID is required and must be positive");
        }
        if (newSubscriptionType == null) {
            throw new IllegalArgumentException("New subscription type is required");
        }
    }
}
