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
import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionCommandServiceImpl.class);
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExternalUserService externalUserService;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                        SubscriptionPlanRepository subscriptionPlanRepository,
                                        ApplicationEventPublisher eventPublisher,
                                        ExternalUserService externalUserService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.eventPublisher = eventPublisher;
        this.externalUserService = externalUserService;
    }

    @Override
    public Optional<Subscription> handle(CreateSubscriptionCommand command) {
        
        if (externalUserService.getUserById(command.userId()).isEmpty()) {
            log.warn("User not found with id: {}", command.userId());
            throw new IllegalArgumentException("User not found with id: " + command.userId());
        }
        
        
        if (subscriptionRepository.existsByUserIdAndStatus(command.userId(), SubscriptionStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active subscription");
        }

        
        Optional<SubscriptionPlan> planOptional = subscriptionPlanRepository.findByPlanType(command.subscriptionType());
        if (planOptional.isEmpty()) {
            throw new IllegalArgumentException("Subscription plan not found for type: " + command.subscriptionType());
        }

        SubscriptionPlan plan = planOptional.get();
        if (!plan.getIsActive()) {
            throw new IllegalArgumentException("Subscription plan is not active: " + command.subscriptionType());
        }

        
        Subscription subscription;
        if (command.paymentReference() != null && !command.paymentReference().trim().isEmpty()) {
            subscription = new Subscription(command.userId(), plan, command.autoRenew(), command.paymentReference());
        } else {
            subscription = new Subscription(command.userId(), plan, command.autoRenew());
        }

        subscription = subscriptionRepository.save(subscription);
        
        
        SubscriptionCreatedEvent event = new SubscriptionCreatedEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            subscription.getSubscriptionPlan().getPrice(),
            "" 
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
        
        
        SubscriptionCancelledEvent cancelEvent = new SubscriptionCancelledEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            command.reason(),
            "" 
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

        
        String oldSubscriptionType = subscription.getSubscriptionPlan().getPlanType().name();

        
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
        
        
        SubscriptionRenewedEvent renewEvent = new SubscriptionRenewedEvent(
            this,
            subscription.getId(),
            subscription.getUserId(),
            oldSubscriptionType,
            subscription.getSubscriptionPlan().getPlanType().name(),
            subscription.getSubscriptionPlan().getName(),
            subscription.getSubscriptionPlan().getPrice(),
            "" 
        );
        eventPublisher.publishEvent(renewEvent);
        
        return Optional.of(subscription);
    }
}
