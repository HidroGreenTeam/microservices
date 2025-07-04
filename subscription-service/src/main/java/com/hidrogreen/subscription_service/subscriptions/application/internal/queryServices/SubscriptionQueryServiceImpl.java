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
import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionQueryServiceImpl.class);
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ExternalUserService externalUserService;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository,
                                      SubscriptionPlanRepository subscriptionPlanRepository,
                                      ExternalUserService externalUserService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.externalUserService = externalUserService;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.subscriptionId());
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByUserIdQuery query) {
        
        if (externalUserService.getUserById(query.userId()).isEmpty()) {
            log.warn("User not found with id: {}", query.userId());
            throw new IllegalArgumentException("User not found with id: " + query.userId());
        }
        
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
