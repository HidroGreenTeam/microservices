package com.hidrogreen.subscription_service.subscriptions.domain.services;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetExpiredSubscriptionsQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByIdQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {
    Optional<Subscription> handle(GetSubscriptionByIdQuery query);
    Optional<Subscription> handle(GetSubscriptionByUserIdQuery query);
    List<SubscriptionPlan> handle(GetAllSubscriptionPlansQuery query);
    List<Subscription> handle(GetExpiredSubscriptionsQuery query);
}
