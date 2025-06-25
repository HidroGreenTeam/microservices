package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform;

import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.CreateSubscriptionResource;

public class CreateSubscriptionCommandFromResourceAssembler {
    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource) {
        return new CreateSubscriptionCommand(
            resource.userId(),
            resource.subscriptionType(),
            resource.autoRenew(),
            resource.paymentReference()
        );
    }
}
