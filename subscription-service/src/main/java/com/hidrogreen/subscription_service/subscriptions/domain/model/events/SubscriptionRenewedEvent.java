package com.hidrogreen.subscription_service.subscriptions.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public final class SubscriptionRenewedEvent extends ApplicationEvent {
    
    private final Long subscriptionId;
    private final Long userId;
    private final String oldSubscriptionType;
    private final String newSubscriptionType;
    private final String planName;
    private final Double price;
    private final String userEmail;
    private final LocalDateTime eventTime;
    
    public SubscriptionRenewedEvent(Object source, Long subscriptionId, Long userId, 
                                  String oldSubscriptionType, String newSubscriptionType,
                                  String planName, Double price, String userEmail) {
        super(source);
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.oldSubscriptionType = oldSubscriptionType;
        this.newSubscriptionType = newSubscriptionType;
        this.planName = planName;
        this.price = price;
        this.userEmail = userEmail;
        this.eventTime = LocalDateTime.now();
    }
}
