package com.hidrogreen.subscription_service.subscriptions.domain.services;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CancelSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.RenewSubscriptionCommand;

import java.util.Optional;

public interface SubscriptionCommandService {
    Optional<Subscription> handle(CreateSubscriptionCommand command);
    Optional<Subscription> handle(CancelSubscriptionCommand command);
    Optional<Subscription> handle(RenewSubscriptionCommand command);
}
