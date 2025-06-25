package com.hidrogreen.subscription_service.subscriptions.application.internal.queryServices;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetExpiredSubscriptionsQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByIdQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.domain.services.SubscriptionQueryService;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPlanRepository;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository,
                                      SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.subscriptionId());
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByUserIdQuery query) {
        return subscriptionRepository.findByUserId(query.userId());
    }

    @Override
    public List<SubscriptionPlan> handle(GetAllSubscriptionPlansQuery query) {
        return subscriptionPlanRepository.findByIsActiveTrue();
    }

    @Override
    public List<Subscription> handle(GetExpiredSubscriptionsQuery query) {
        return subscriptionRepository.findExpiredSubscriptions(new Date(), SubscriptionStatus.ACTIVE);
    }
}
