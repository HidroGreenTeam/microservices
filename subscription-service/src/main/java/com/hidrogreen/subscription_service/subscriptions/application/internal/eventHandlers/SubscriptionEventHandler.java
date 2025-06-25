package com.hidrogreen.subscription_service.subscriptions.application.internal.eventHandlers;

import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionCreatedEvent;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionCancelledEvent;
import com.hidrogreen.subscription_service.subscriptions.domain.model.events.SubscriptionRenewedEvent;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.dto.SubscriptionNotificationDto;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.publisher.SubscriptionNotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class SubscriptionEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionEventHandler.class);
    
    private final SubscriptionNotificationPublisher notificationPublisher;
    private final ExternalUserService externalUserService;

    public SubscriptionEventHandler(SubscriptionNotificationPublisher notificationPublisher,
                                  ExternalUserService externalUserService) {
        this.notificationPublisher = notificationPublisher;
        this.externalUserService = externalUserService;
    }

    @EventListener
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        LOGGER.info("Handling subscription created event for subscription ID: {}", event.getSubscriptionId());
        
        try {
            // Get user information
            Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(event.getUserId());
            
            String userEmail = event.getUserEmail();
            String userName = "User";
            
            if (userInfoOpt.isPresent()) {
                ExternalUserService.UserInfo userInfo = userInfoOpt.get();
                userEmail = userInfo.email();
                userName = userInfo.firstName() + " " + userInfo.lastName();
            }
            
            // Generate invoice number
            String invoiceNumber = generateInvoiceNumber(event.getSubscriptionId(), event.getEventTime());
            
            // Create notification DTO
            SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
            notification.setNotificationType("SUBSCRIPTION_CREATED");
            notification.setUserId(event.getUserId());
            notification.setUserEmail(userEmail);
            notification.setUserName(userName);
            notification.setSubscriptionId(event.getSubscriptionId());
            notification.setSubscriptionType(event.getSubscriptionType());
            notification.setPlanName(event.getPlanName());
            notification.setPrice(event.getPrice());
            notification.setCurrency("USD");
            notification.setEventTime(event.getEventTime());
            notification.setSubject("Confirmación de Suscripción - HidroGreen");
            notification.setInvoiceNumber(invoiceNumber);
            notification.setFeatures(getPlanFeatures(event.getSubscriptionType()));
            
            // Publish notification
            notificationPublisher.publishSubscriptionCreated(notification);
            
            LOGGER.info("Successfully handled subscription created event for subscription ID: {}", event.getSubscriptionId());
            
        } catch (Exception e) {
            LOGGER.error("Failed to handle subscription created event for subscription ID: {}", event.getSubscriptionId(), e);
        }
    }
    
    private String generateInvoiceNumber(Long subscriptionId, LocalDateTime eventTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = eventTime.format(formatter);
        return "INV-" + date + "-" + String.format("%06d", subscriptionId);
    }
    
    private String getPlanFeatures(String subscriptionType) {
        return switch (subscriptionType) {
            case "FREE" -> "• 1 cultivo\n• 5 reportes\n• Soporte básico";
            case "BASIC" -> "• 5 cultivos\n• 20 reportes\n• Soporte estándar";
            case "PREMIUM" -> "• 15 cultivos\n• 50 reportes\n• Soporte prioritario\n• Análisis avanzado";
            case "ENTERPRISE" -> "• Cultivos ilimitados\n• Reportes ilimitados\n• Soporte prioritario\n• Análisis avanzado\n• Integraciones personalizadas";
            default -> "Características del plan";
        };
    }
    
    @EventListener
    public void handleSubscriptionCancelled(SubscriptionCancelledEvent event) {
        LOGGER.info("Handling subscription cancelled event for subscription ID: {}", event.getSubscriptionId());
        
        try {
            // Get user information
            Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(event.getUserId());
            
            String userEmail = event.getUserEmail();
            String userName = "User";
            
            if (userInfoOpt.isPresent()) {
                ExternalUserService.UserInfo userInfo = userInfoOpt.get();
                userEmail = userInfo.email();
                userName = userInfo.firstName() + " " + userInfo.lastName();
            }
            
            // Create notification DTO
            SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
            notification.setNotificationType("SUBSCRIPTION_CANCELLED");
            notification.setUserId(event.getUserId());
            notification.setUserEmail(userEmail);
            notification.setUserName(userName);
            notification.setSubscriptionId(event.getSubscriptionId());
            notification.setSubscriptionType(event.getSubscriptionType());
            notification.setPlanName(event.getPlanName());
            notification.setEventTime(event.getEventTime());
            notification.setSubject("Cancelación de Suscripción - HidroGreen");
            
            // Publish notification
            notificationPublisher.publishSubscriptionCancelled(notification);
            
            LOGGER.info("Successfully handled subscription cancelled event for subscription ID: {}", event.getSubscriptionId());
            
        } catch (Exception e) {
            LOGGER.error("Failed to handle subscription cancelled event for subscription ID: {}", event.getSubscriptionId(), e);
        }
    }
    
    @EventListener
    public void handleSubscriptionRenewed(SubscriptionRenewedEvent event) {
        LOGGER.info("Handling subscription renewed event for subscription ID: {}", event.getSubscriptionId());
        
        try {
            // Get user information
            Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(event.getUserId());
            
            String userEmail = event.getUserEmail();
            String userName = "User";
            
            if (userInfoOpt.isPresent()) {
                ExternalUserService.UserInfo userInfo = userInfoOpt.get();
                userEmail = userInfo.email();
                userName = userInfo.firstName() + " " + userInfo.lastName();
            }
            
            // Generate invoice number
            String invoiceNumber = generateInvoiceNumber(event.getSubscriptionId(), event.getEventTime());
            
            // Create notification DTO
            SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
            notification.setNotificationType("SUBSCRIPTION_RENEWED");
            notification.setUserId(event.getUserId());
            notification.setUserEmail(userEmail);
            notification.setUserName(userName);
            notification.setSubscriptionId(event.getSubscriptionId());
            notification.setSubscriptionType(event.getNewSubscriptionType());
            notification.setPlanName(event.getPlanName());
            notification.setPrice(event.getPrice());
            notification.setCurrency("USD");
            notification.setEventTime(event.getEventTime());
            notification.setSubject("Renovación de Suscripción - HidroGreen");
            notification.setInvoiceNumber(invoiceNumber);
            notification.setFeatures(getPlanFeatures(event.getNewSubscriptionType()));
            
            // Publish notification
            notificationPublisher.publishSubscriptionRenewed(notification);
            
            LOGGER.info("Successfully handled subscription renewed event for subscription ID: {}", event.getSubscriptionId());
            
        } catch (Exception e) {
            LOGGER.error("Failed to handle subscription renewed event for subscription ID: {}", event.getSubscriptionId(), e);
        }
    }
}
