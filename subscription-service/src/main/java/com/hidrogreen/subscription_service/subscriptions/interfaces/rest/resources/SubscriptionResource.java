package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources;

import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;

import java.util.Date;

public record SubscriptionResource(
    Long id,
    Long userId,
    SubscriptionType subscriptionType,
    String subscriptionPlanName,
    SubscriptionStatus status,
    Date startDate,
    Date endDate,
    Boolean autoRenew,
    String paymentReference,
    Integer daysRemaining,
    Boolean isActive,
    Boolean isExpired,
    String cancellationReason,
    Date cancelledAt,
    Date createdAt,
    Date updatedAt
) {
}
