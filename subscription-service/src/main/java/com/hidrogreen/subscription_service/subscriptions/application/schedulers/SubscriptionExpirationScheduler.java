package com.hidrogreen.subscription_service.subscriptions.application.schedulers;

import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.dto.SubscriptionNotificationDto;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.publisher.SubscriptionNotificationPublisher;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;


@Component
public class SubscriptionExpirationScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionExpirationScheduler.class);

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionNotificationPublisher notificationPublisher;
    private final ExternalUserService externalUserService;

    public SubscriptionExpirationScheduler(SubscriptionRepository subscriptionRepository,
            SubscriptionNotificationPublisher notificationPublisher,
            ExternalUserService externalUserService) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationPublisher = notificationPublisher;
        this.externalUserService = externalUserService;
    }

    
    @Scheduled(cron = "0 0 9 * * *")
    public void checkExpiringSubscriptions() {
        LOGGER.info("Starting scheduled check for expiring subscriptions at: {}", LocalDateTime.now());

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expirationThreshold = now.plusDays(7); 

            
            List<Subscription> expiringSubscriptions = subscriptionRepository
                    .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE, now, expirationThreshold);

            LOGGER.info("Found {} subscriptions expiring within 7 days", expiringSubscriptions.size());

            for (Subscription subscription : expiringSubscriptions) {
                try {
                    sendExpirationNotification(subscription);
                } catch (Exception e) {
                    LOGGER.error("Failed to send expiration notification for subscription ID: {}",
                            subscription.getId(), e);
                }
            }

            LOGGER.info("Completed scheduled check for expiring subscriptions");

        } catch (Exception e) {
            LOGGER.error("Error during scheduled expiration check", e);
        }
    }

    
    @Scheduled(fixedRate = 14400000) 
    public void checkCriticalExpiringSubscriptions() {
        LOGGER.info("Starting critical expiration check at: {}", LocalDateTime.now());

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime criticalThreshold = now.plusHours(24); 

            
            List<Subscription> criticallyExpiringSubscriptions = subscriptionRepository
                    .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE, now, criticalThreshold);

            LOGGER.info("Found {} subscriptions expiring within 24 hours", criticallyExpiringSubscriptions.size());

            for (Subscription subscription : criticallyExpiringSubscriptions) {
                try {
                    sendCriticalExpirationNotification(subscription);
                } catch (Exception e) {
                    LOGGER.error("Failed to send critical expiration notification for subscription ID: {}",
                            subscription.getId(), e);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error during critical expiration check", e);
        }
    }

    private void sendExpirationNotification(Subscription subscription) {
        LOGGER.info("Sending expiration notification for subscription ID: {}", subscription.getId());

        
        Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(subscription.getUserId());

        String userEmail = "unknown@email.com";
        String userName = "Usuario";

        if (userInfoOpt.isPresent()) {
            ExternalUserService.UserInfo userInfo = userInfoOpt.get();
            userEmail = userInfo.email();
            userName = userInfo.firstName() + " " + userInfo.lastName();
        }

        
        SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
        notification.setNotificationType("SUBSCRIPTION_EXPIRING");
        notification.setUserId(subscription.getUserId());
        notification.setUserEmail(userEmail);
        notification.setUserName(userName);
        notification.setSubscriptionId(subscription.getId());
        notification.setSubscriptionType(subscription.getSubscriptionPlan().getPlanType().name());
        notification.setPlanName(subscription.getSubscriptionPlan().getName());
        notification.setPrice(subscription.getSubscriptionPlan().getPrice());
        notification.setCurrency("USD");
        notification.setEndDate(subscription.getEndDate().toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime());
        notification.setEventTime(LocalDateTime.now());
        notification.setSubject("⏰ Tu suscripción vence pronto - HidroGreen");
        notification.setFeatures(getPlanFeatures(subscription.getSubscriptionPlan().getPlanType().name()));

        
        notificationPublisher.publishSubscriptionCreated(notification); 

        LOGGER.info("Expiration notification sent for subscription ID: {}", subscription.getId());
    }

    private void sendCriticalExpirationNotification(Subscription subscription) {
        LOGGER.info("Sending CRITICAL expiration notification for subscription ID: {}", subscription.getId());

        
        Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(subscription.getUserId());

        String userEmail = "unknown@email.com";
        String userName = "Usuario";

        if (userInfoOpt.isPresent()) {
            ExternalUserService.UserInfo userInfo = userInfoOpt.get();
            userEmail = userInfo.email();
            userName = userInfo.firstName() + " " + userInfo.lastName();
        }

        
        SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
        notification.setNotificationType("SUBSCRIPTION_EXPIRING");
        notification.setUserId(subscription.getUserId());
        notification.setUserEmail(userEmail);
        notification.setUserName(userName);
        notification.setSubscriptionId(subscription.getId());
        notification.setSubscriptionType(subscription.getSubscriptionPlan().getPlanType().name());
        notification.setPlanName(subscription.getSubscriptionPlan().getName());
        notification.setPrice(subscription.getSubscriptionPlan().getPrice());
        notification.setCurrency("USD");
        notification.setEndDate(subscription.getEndDate().toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime());
        notification.setEventTime(LocalDateTime.now());
        notification.setSubject("🚨 URGENTE: Tu suscripción vence en menos de 24 horas - HidroGreen");
        notification.setFeatures(getPlanFeatures(subscription.getSubscriptionPlan().getPlanType().name()));

        
        notificationPublisher.publishSubscriptionCreated(notification);

        LOGGER.info("Critical expiration notification sent for subscription ID: {}", subscription.getId());
    }

    private String getPlanFeatures(String subscriptionType) {
        return switch (subscriptionType) {
            case "FREE" -> "• 1 cultivo\n• 5 reportes\n• Soporte básico";
            case "BASIC" -> "• 5 cultivos\n• 20 reportes\n• Soporte estándar";
            case "PREMIUM" -> "• 15 cultivos\n• 50 reportes\n• Soporte prioritario\n• Análisis avanzado";
            case "ENTERPRISE" ->
                "• Cultivos ilimitados\n• Reportes ilimitados\n• Soporte prioritario\n• Análisis avanzado\n• Integraciones personalizadas";
            default -> "Características del plan";
        };
    }
}
