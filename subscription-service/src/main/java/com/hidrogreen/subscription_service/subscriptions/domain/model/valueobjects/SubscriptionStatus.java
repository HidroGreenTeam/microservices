package com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects;

public enum SubscriptionStatus {
    ACTIVE("Active", "Subscription is currently active"),
    EXPIRED("Expired", "Subscription has expired"),
    CANCELLED("Cancelled", "Subscription has been cancelled"),
    SUSPENDED("Suspended", "Subscription is temporarily suspended"),
    PENDING_PAYMENT("Pending Payment", "Waiting for payment confirmation");

    private final String displayName;
    private final String description;

    SubscriptionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
