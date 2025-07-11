package com.hidrogreen.subscription_service.subscriptions.domain.model.commands;

public record ActivateSubscriptionCommand(
    Long subscriptionId,
    String paymentReference
) {
    public ActivateSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
        if (paymentReference == null || paymentReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment reference cannot be null or empty");
        }
    }
} 