package com.hidrogreen.subscription_service.subscriptions.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public final class SubscriptionActivatedEvent extends ApplicationEvent {
    private final Long subscriptionId;
    private final Long userId;
    private final String subscriptionType;
    private final String planName;
    private final BigDecimal price;
    private final String paymentReference;
    private final LocalDateTime eventTime;

    public SubscriptionActivatedEvent(Object source, Long subscriptionId, Long userId, 
                                    String subscriptionType, String planName, 
                                    BigDecimal price, String paymentReference) {
        super(source);
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.subscriptionType = subscriptionType;
        this.planName = planName;
        this.price = price;
        this.paymentReference = paymentReference;
        this.eventTime = LocalDateTime.now();
    }
} 