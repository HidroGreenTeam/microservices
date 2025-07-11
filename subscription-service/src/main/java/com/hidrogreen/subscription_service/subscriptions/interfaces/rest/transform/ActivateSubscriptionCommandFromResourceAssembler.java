package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform;

import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.ActivateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.ActivateSubscriptionResource;

public class ActivateSubscriptionCommandFromResourceAssembler {
    
    public static ActivateSubscriptionCommand toCommandFromResource(ActivateSubscriptionResource resource) {
        return new ActivateSubscriptionCommand(
            null, // subscriptionId se pasará desde el path parameter
            resource.getPaymentReference()
        );
    }
} 