package com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects;

public enum SubscriptionType {
    FREE("Free Plan", 0.0, 30, "Basic features with limited access"),
    BASIC("Basic Plan", 9.99, 30, "Enhanced features for individual farmers"),
    PREMIUM("Premium Plan", 19.99, 30, "Advanced features with priority support"),
    ENTERPRISE("Enterprise Plan", 49.99, 30, "Full access with custom integrations");

    private final String displayName;
    private final Double price;
    private final Integer durationDays;
    private final String description;

    SubscriptionType(String displayName, Double price, Integer durationDays, String description) {
        this.displayName = displayName;
        this.price = price;
        this.durationDays = durationDays;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public String getDescription() {
        return description;
    }
}
