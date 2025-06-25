package com.hidrogreen.subscription_service.subscriptions.application.schedulers;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler to automatically expire subscriptions that have passed their end date
 */
@Component
public class SubscriptionMaintenanceScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionMaintenanceScheduler.class);
    
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionMaintenanceScheduler(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Check for subscriptions that have expired and update their status
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void expireOldSubscriptions() {
        LOGGER.info("Starting scheduled check for expired subscriptions at: {}", LocalDateTime.now());
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Find active subscriptions that have passed their end date
            List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE, 
                                            LocalDateTime.MIN, // From the beginning of time
                                            now); // Up to now
            
            LOGGER.info("Found {} expired subscriptions to update", expiredSubscriptions.size());
            
            int updatedCount = 0;
            for (Subscription subscription : expiredSubscriptions) {
                if (subscription.getEndDate().isBefore(now)) {
                    try {
                        subscription.expire(); // Assuming we add this method to the domain
                        subscriptionRepository.save(subscription);
                        updatedCount++;
                        
                        LOGGER.debug("Expired subscription ID: {} for user: {}", 
                                   subscription.getId(), subscription.getUserId());
                    } catch (Exception e) {
                        LOGGER.error("Failed to expire subscription ID: {}", subscription.getId(), e);
                    }
                }
            }
            
            LOGGER.info("Successfully expired {} subscriptions", updatedCount);
            
        } catch (Exception e) {
            LOGGER.error("Error during scheduled subscription expiration check", e);
        }
    }

    /**
     * Cleanup task to remove very old cancelled/expired subscriptions
     * Runs monthly on the 1st at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void cleanupOldSubscriptions() {
        LOGGER.info("Starting monthly cleanup of old subscriptions at: {}", LocalDateTime.now());
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(12); // Keep 12 months of history
            
            // Note: This is a soft cleanup - we might want to archive rather than delete
            // For now, we'll just log what we would clean up
            
            List<Subscription> oldCancelledSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBetween(SubscriptionStatus.CANCELLED, 
                                            LocalDateTime.MIN, 
                                            cutoffDate);
            
            List<Subscription> oldExpiredSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBetween(SubscriptionStatus.EXPIRED, 
                                            LocalDateTime.MIN, 
                                            cutoffDate);
            
            int totalOldSubscriptions = oldCancelledSubscriptions.size() + oldExpiredSubscriptions.size();
            
            LOGGER.info("Found {} old subscriptions that could be archived " +
                       "(cancelled: {}, expired: {})", 
                       totalOldSubscriptions, 
                       oldCancelledSubscriptions.size(), 
                       oldExpiredSubscriptions.size());
            
            // TODO: Implement archiving logic here if needed
            // For now, we just log the count for monitoring purposes
            
        } catch (Exception e) {
            LOGGER.error("Error during monthly subscription cleanup", e);
        }
    }
}
