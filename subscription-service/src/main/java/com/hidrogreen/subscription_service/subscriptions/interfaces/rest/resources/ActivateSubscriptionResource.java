package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources;

public class ActivateSubscriptionResource {
    private String paymentReference;

    public ActivateSubscriptionResource() {
    }

    public ActivateSubscriptionResource(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
} 