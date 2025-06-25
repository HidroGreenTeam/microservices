package com.hidrogreen.subscription_service.subscriptions.application.internal.commandServices;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CancelSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.RenewSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionCreatedEvent;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionCancelledEvent;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionRenewedEvent;
import com.hidrogreen.subscription_service.subscriptions.domain.services.SubscriptionCommandService;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPlanRepository;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                        SubscriptionPlanRepository subscriptionPlanRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Subscription> handle(CreateSubscriptionCommand command) {
        // Check if user already has an active subscription
        if (subscriptionRepository.existsByUserIdAndStatus(command.userId(), SubscriptionStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active subscription");
        }

        // Get the subscription plan
        Optional<SubscriptionPlan> planOptional = subscriptionPlanRepository.findByPlanType(command.subscriptionType());
        if (planOptional.isEmpty()) {
            throw new IllegalArgumentException("Subscription plan not found for type: " + command.subscriptionType());
        }

        SubscriptionPlan plan = planOptional.get();
        if (!plan.getIsActive()) {
            throw new IllegalArgumentException("Subscription plan is not active: " + command.subscriptionType());
        }

        // Create subscription
        Subscription subscription;
        if (command.paymentReference() != null && !command.paymentReference().trim().isEmpty()) {
            subscription = new Subscription(command.userId(), plan, command.autoRenew(), command.paymentReference());
        } else {
            subscription = new Subscription(command.userId(), plan, command.autoRenew());
        }

        subscription = subscriptionRepository.save(subscription);
        
        // Publish subscription created event
        SubscriptionCreatedEvent event = new SubscriptionCreatedEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            subscription.getSubscriptionPlan().getPrice(),
            "" // Email will be fetched by the event handler
        );
        eventPublisher.publishEvent(event);
        
        return Optional.of(subscription);
    }

    @Override
    public Optional<Subscription> handle(CancelSubscriptionCommand command) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(command.subscriptionId());
        if (subscriptionOptional.isEmpty()) {
            return Optional.empty();
        }

        Subscription subscription = subscriptionOptional.get();
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is already cancelled");
        }

        subscription.cancel(command.reason());
        subscription = subscriptionRepository.save(subscription);
        
        // Publish subscription cancelled event
        SubscriptionCancelledEvent cancelEvent = new SubscriptionCancelledEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            command.reason(),
            "" // Email will be fetched by the event handler
        );
        eventPublisher.publishEvent(cancelEvent);
        
        return Optional.of(subscription);
    }

    @Override
    public Optional<Subscription> handle(RenewSubscriptionCommand command) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(command.subscriptionId());
        if (subscriptionOptional.isEmpty()) {
            return Optional.empty();
        }

        Subscription subscription = subscriptionOptional.get();
        if (!subscription.canRenew()) {
            throw new IllegalStateException("Subscription cannot be renewed");
        }

        // Store old subscription type for event
        String oldSubscriptionType = subscription.getSubscriptionPlan().getPlanType().name();

        // Get the new subscription plan
        Optional<SubscriptionPlan> planOptional = subscriptionPlanRepository.findByPlanType(command.newSubscriptionType());
        if (planOptional.isEmpty()) {
            throw new IllegalArgumentException("Subscription plan not found for type: " + command.newSubscriptionType());
        }

        SubscriptionPlan newPlan = planOptional.get();
        if (!newPlan.getIsActive()) {
            throw new IllegalArgumentException("Subscription plan is not active: " + command.newSubscriptionType());
        }

        subscription.renew(newPlan);
        if (command.paymentReference() != null && !command.paymentReference().trim().isEmpty()) {
            subscription.setPaymentReference(command.paymentReference());
        }

        subscription = subscriptionRepository.save(subscription);
        
        // Publish subscription renewed event
        SubscriptionRenewedEvent renewEvent = new SubscriptionRenewedEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            oldSubscriptionType,
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            subscription.getSubscriptionPlan().getPrice(),
            "" // Email will be fetched by the event handler
        );
        eventPublisher.publishEvent(renewEvent);
        
        return Optional.of(subscription);
    }
}
