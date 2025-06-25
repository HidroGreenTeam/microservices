package com.hidrogreen.subscription_service.subscriptions.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    Optional<SubscriptionPlan> findByPlanType(SubscriptionType planType);
    
    List<SubscriptionPlan> findByIsActiveTrue();
    
    boolean existsByPlanType(SubscriptionType planType);
}
