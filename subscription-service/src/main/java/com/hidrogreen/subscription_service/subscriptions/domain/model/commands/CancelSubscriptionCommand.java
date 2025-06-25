package com.hidrogreen.subscription_service.subscriptions.domain.model.commands;

public record CancelSubscriptionCommand(
    Long subscriptionId,
    String reason
) {
    public CancelSubscriptionCommand {
        if (subscriptionId == null || subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID is required and must be positive");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }
}
