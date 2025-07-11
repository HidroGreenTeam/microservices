package com.hidrogreen.subscription_service.subscriptions.interfaces.rest;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CancelSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.RenewSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.commands.ActivateSubscriptionCommand;
import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByIdQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;
import com.hidrogreen.subscription_service.subscriptions.domain.services.SubscriptionCommandService;
import com.hidrogreen.subscription_service.subscriptions.domain.services.SubscriptionQueryService;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.CreateSubscriptionResource;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.SubscriptionPlanResource;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.SubscriptionResource;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.resources.ActivateSubscriptionResource;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform.SubscriptionPlanResourceFromEntityAssembler;
import com.hidrogreen.subscription_service.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription Management Endpoints")
public class SubscriptionController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionController(SubscriptionCommandService subscriptionCommandService,
                                SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new subscription")
    public ResponseEntity<SubscriptionResource> createSubscription(@RequestBody CreateSubscriptionResource resource) {
        try {
            CreateSubscriptionCommand command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
            Optional<Subscription> subscription = subscriptionCommandService.handle(command);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResource> getSubscriptionById(@PathVariable Long subscriptionId) {
        try {
            GetSubscriptionByIdQuery query = new GetSubscriptionByIdQuery(subscriptionId);
            Optional<Subscription> subscription = subscriptionQueryService.handle(query);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get subscription by user ID")
    public ResponseEntity<SubscriptionResource> getSubscriptionByUserId(@PathVariable Long userId) {
        try {
            GetSubscriptionByUserIdQuery query = new GetSubscriptionByUserIdQuery(userId);
            Optional<Subscription> subscription = subscriptionQueryService.handle(query);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{subscriptionId}/cancel")
    @Operation(summary = "Cancel a subscription")
    public ResponseEntity<SubscriptionResource> cancelSubscription(@PathVariable Long subscriptionId, 
                                                                 @RequestParam String reason) {
        try {
            CancelSubscriptionCommand command = new CancelSubscriptionCommand(subscriptionId, reason);
            Optional<Subscription> subscription = subscriptionCommandService.handle(command);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{subscriptionId}/renew")
    @Operation(summary = "Renew a subscription")
    public ResponseEntity<SubscriptionResource> renewSubscription(@PathVariable Long subscriptionId,
                                                                @RequestParam SubscriptionType newSubscriptionType,
                                                                @RequestParam(required = false) String paymentReference) {
        try {
            RenewSubscriptionCommand command = new RenewSubscriptionCommand(subscriptionId, newSubscriptionType, paymentReference);
            Optional<Subscription> subscription = subscriptionCommandService.handle(command);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{subscriptionId}/activate")
    @Operation(summary = "Activate a subscription with payment reference")
    public ResponseEntity<Object> activateSubscription(@PathVariable Long subscriptionId,
                                                     @RequestBody ActivateSubscriptionResource resource) {
        try {
            // Crear comando directamente con subscriptionId del path y paymentReference del body
            ActivateSubscriptionCommand command = new ActivateSubscriptionCommand(subscriptionId, resource.getPaymentReference());
            
            Optional<Subscription> subscription = subscriptionCommandService.handle(command);
            if (subscription.isPresent()) {
                SubscriptionResource subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
                return new ResponseEntity<>(subscriptionResource, HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("error", "Subscription not found"), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Internal server error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/plans")
    @Operation(summary = "Get all available subscription plans")
    public ResponseEntity<List<SubscriptionPlanResource>> getAllSubscriptionPlans() {
        try {
            GetAllSubscriptionPlansQuery query = new GetAllSubscriptionPlansQuery();
            List<SubscriptionPlan> plans = subscriptionQueryService.handle(query);
            List<SubscriptionPlanResource> planResources = plans.stream()
                .map(SubscriptionPlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
            return new ResponseEntity<>(planResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
