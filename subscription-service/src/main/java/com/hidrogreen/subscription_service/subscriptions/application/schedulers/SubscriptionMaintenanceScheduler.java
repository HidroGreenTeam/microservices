package com.hidrogreen.subscription_service.subscriptions.application.schedulers;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Component
public class SubscriptionMaintenanceScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionMaintenanceScheduler.class);

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionMaintenanceScheduler(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    
    @Scheduled(fixedRate = 3600000) 
    @Transactional
    public void expireOldSubscriptions() {
        LOGGER.info("Starting scheduled check for expired subscriptions at: {}", LocalDateTime.now());

        try {
            LocalDateTime now = LocalDateTime.now();
            Date nowAsDate = (Date) Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            Date beginningOfTime = (Date) Date.from(LocalDateTime.MIN.atZone(ZoneId.systemDefault()).toInstant());

            
            List<Subscription> expiredSubscriptions = subscriptionRepository
                    .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE,
                            beginningOfTime,
                            nowAsDate);

            LOGGER.info("Found {} expired subscriptions to update", expiredSubscriptions.size());

            int updatedCount = 0;
            for (Subscription subscription : expiredSubscriptions) {
                if (subscription.getEndDate().before(nowAsDate)) {
                    try {
                        subscription.expire();
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

    
    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void cleanupOldSubscriptions() {
        LOGGER.info("Starting monthly cleanup of old subscriptions at: {}", LocalDateTime.now());

        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(12); 

            
            

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

            
            

        } catch (Exception e) {
            LOGGER.error("Error during monthly subscription cleanup", e);
        }
    }
}
