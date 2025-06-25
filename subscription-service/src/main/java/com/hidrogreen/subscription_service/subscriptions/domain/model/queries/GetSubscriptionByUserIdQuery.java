package com.hidrogreen.subscription_service.subscriptions.domain.model.queries;

public record GetSubscriptionByUserIdQuery(Long userId) {
    public GetSubscriptionByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required and must be positive");
        }
    }
}
