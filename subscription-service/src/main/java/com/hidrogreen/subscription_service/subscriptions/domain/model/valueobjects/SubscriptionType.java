package com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects;

import java.math.BigDecimal;

public enum SubscriptionType {
    FREE("Free Plan", BigDecimal.valueOf(0.00), 30, "Basic features with limited access"),
    BASIC("Basic Plan", BigDecimal.valueOf(9.99), 30, "Enhanced features for individual farmers"),
    PREMIUM("Premium Plan", BigDecimal.valueOf(19.99), 30, "Advanced features with priority support"),
    ENTERPRISE("Enterprise Plan", BigDecimal.valueOf(49.99), 30, "Full access with custom integrations");

    private final String displayName;
    private final BigDecimal price;
    private final Integer durationDays;
    private final String description;

    SubscriptionType(String displayName, BigDecimal price, Integer durationDays, String description) {
        this.displayName = displayName;
        this.price = price;
        this.durationDays = durationDays;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public String getDescription() {
        return description;
    }
}
