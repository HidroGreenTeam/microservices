package com.hidrogreen.subscription_service.subscriptions.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public final class SubscriptionCancelledEvent extends ApplicationEvent {
    
    private final Long subscriptionId;
    private final Long userId;
    private final String subscriptionType;
    private final String planName;
    private final String cancellationReason;
    private final String userEmail;
    private final LocalDateTime eventTime;
    
    public SubscriptionCancelledEvent(Object source, Long subscriptionId, Long userId, 
                                    String subscriptionType, String planName, 
                                    String cancellationReason, String userEmail) {
        super(source);
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.subscriptionType = subscriptionType;
        this.planName = planName;
        this.cancellationReason = cancellationReason;
        this.userEmail = userEmail;
        this.eventTime = LocalDateTime.now();
    }
}
