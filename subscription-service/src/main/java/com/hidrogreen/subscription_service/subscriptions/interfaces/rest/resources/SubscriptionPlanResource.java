package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources;

import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;

import java.util.Date;

public record SubscriptionPlanResource(
    Long id,
    SubscriptionType planType,
    String name,
    String description,
    Double price,
    Integer durationDays,
    Integer maxCrops,
    Integer maxReports,
    Boolean hasPrioritySupport,
    Boolean hasAdvancedAnalytics,
    Boolean isActive,
    Date createdAt,
    Date updatedAt
) {
}
