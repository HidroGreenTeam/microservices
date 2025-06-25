package com.hidrogreen.subscription_service.subscriptions.domain.model.commands;

import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;

public record CreateSubscriptionCommand(
    Long userId,
    SubscriptionType subscriptionType,
    Boolean autoRenew,
    String paymentReference
) {
    public CreateSubscriptionCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required and must be positive");
        }
        if (subscriptionType == null) {
            throw new IllegalArgumentException("Subscription type is required");
        }
        if (autoRenew == null) {
            autoRenew = false;
        }
    }
}
