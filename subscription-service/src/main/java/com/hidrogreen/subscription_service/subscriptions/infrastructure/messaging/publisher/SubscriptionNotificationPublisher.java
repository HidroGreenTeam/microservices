package com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.publisher;

import com.hidrogreen.subscription_service.shared.infrastructure.messaging.rabbitmq.config.RabbitMQConfig;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.dto.SubscriptionNotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionNotificationPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionNotificationPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public SubscriptionNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishSubscriptionCreated(SubscriptionNotificationDto notification) {
        try {
            LOGGER.info("Publishing subscription created notification for user ID: {}, subscription ID: {}", 
                       notification.getUserId(), notification.getSubscriptionId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                RabbitMQConfig.SUBSCRIPTION_CREATED_ROUTING_KEY,
                notification
            );
            
            LOGGER.info("Successfully published subscription created notification for subscription ID: {}", 
                       notification.getSubscriptionId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish subscription created notification for subscription ID: {}", 
                        notification.getSubscriptionId(), e);
        }
    }

    public void publishSubscriptionCancelled(SubscriptionNotificationDto notification) {
        try {
            LOGGER.info("Publishing subscription cancelled notification for user ID: {}, subscription ID: {}", 
                       notification.getUserId(), notification.getSubscriptionId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                RabbitMQConfig.SUBSCRIPTION_CANCELLED_ROUTING_KEY,
                notification
            );
            
            LOGGER.info("Successfully published subscription cancelled notification for subscription ID: {}", 
                       notification.getSubscriptionId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish subscription cancelled notification for subscription ID: {}", 
                        notification.getSubscriptionId(), e);
        }
    }

    public void publishSubscriptionRenewed(SubscriptionNotificationDto notification) {
        try {
            LOGGER.info("Publishing subscription renewed notification for user ID: {}, subscription ID: {}", 
                       notification.getUserId(), notification.getSubscriptionId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                RabbitMQConfig.SUBSCRIPTION_RENEWED_ROUTING_KEY,
                notification
            );
            
            LOGGER.info("Successfully published subscription renewed notification for subscription ID: {}", 
                       notification.getSubscriptionId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish subscription renewed notification for subscription ID: {}", 
                        notification.getSubscriptionId(), e);
        }
    }

    public void publishSubscriptionActivated(SubscriptionNotificationDto notification) {
        try {
            LOGGER.info("Publishing subscription activated notification for user ID: {}, subscription ID: {}", 
                       notification.getUserId(), notification.getSubscriptionId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                RabbitMQConfig.SUBSCRIPTION_ACTIVATED_ROUTING_KEY,
                notification
            );
            
            LOGGER.info("Successfully published subscription activated notification for subscription ID: {}", 
                       notification.getSubscriptionId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish subscription activated notification for subscription ID: {}", 
                        notification.getSubscriptionId(), e);
        }
    }
}
