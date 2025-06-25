package com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates.Subscription;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    Optional<Subscription> findByUserId(Long userId);
    
    List<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    
    List<Subscription> findByStatus(SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.status = :status")
    List<Subscription> findExpiredSubscriptions(@Param("currentDate") Date currentDate, 
                                              @Param("status") SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsForRenewal(@Param("startDate") Date startDate, 
                                                 @Param("endDate") Date endDate);
    
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findByStatusAndEndDateBetween(@Param("status") SubscriptionStatus status,
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
}
