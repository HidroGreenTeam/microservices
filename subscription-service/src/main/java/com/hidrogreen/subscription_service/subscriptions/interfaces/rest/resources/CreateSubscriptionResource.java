package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources;

import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;

public record CreateSubscriptionResource(
    Long userId,
    SubscriptionType subscriptionType,
    Boolean autoRenew,
    String paymentReference
) {
}
