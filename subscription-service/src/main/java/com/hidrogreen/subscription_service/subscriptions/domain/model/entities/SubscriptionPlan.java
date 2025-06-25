package com.hidrogreen.subscription_service.subscriptions.domain.model.entities;

import com.hidrogreen.subscription_service.shared.domain.model.entities.AuditableModel;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription_plans")
@NoArgsConstructor
public class SubscriptionPlan extends AuditableModel {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, unique = true)
    private SubscriptionType planType;

    @Getter
    @Setter
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Getter
    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Double price;

    @Getter
    @Setter
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Getter
    @Setter
    @Column(name = "max_crops", nullable = false)
    private Integer maxCrops;

    @Getter
    @Setter
    @Column(name = "max_reports", nullable = false)
    private Integer maxReports;

    @Getter
    @Setter
    @Column(name = "has_priority_support", nullable = false)
    private Boolean hasPrioritySupport;

    @Getter
    @Setter
    @Column(name = "has_advanced_analytics", nullable = false)
    private Boolean hasAdvancedAnalytics;

    @Getter
    @Setter
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public SubscriptionPlan(SubscriptionType planType, String name, String description, 
                           Double price, Integer durationDays, Integer maxCrops, 
                           Integer maxReports, Boolean hasPrioritySupport, 
                           Boolean hasAdvancedAnalytics, Boolean isActive) {
        this.planType = planType;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.maxCrops = maxCrops;
        this.maxReports = maxReports;
        this.hasPrioritySupport = hasPrioritySupport;
        this.hasAdvancedAnalytics = hasAdvancedAnalytics;
        this.isActive = isActive;
    }

    public SubscriptionPlan(SubscriptionType planType) {
        this.planType = planType;
        this.name = planType.getDisplayName();
        this.description = planType.getDescription();
        this.price = planType.getPrice();
        this.durationDays = planType.getDurationDays();
        this.isActive = true;
        
        // Set default limits based on plan type
        switch (planType) {
            case FREE:
                this.maxCrops = 1;
                this.maxReports = 5;
                this.hasPrioritySupport = false;
                this.hasAdvancedAnalytics = false;
                break;
            case BASIC:
                this.maxCrops = 5;
                this.maxReports = 20;
                this.hasPrioritySupport = false;
                this.hasAdvancedAnalytics = false;
                break;
            case PREMIUM:
                this.maxCrops = 15;
                this.maxReports = 50;
                this.hasPrioritySupport = true;
                this.hasAdvancedAnalytics = true;
                break;
            case ENTERPRISE:
                this.maxCrops = -1; // unlimited
                this.maxReports = -1; // unlimited
                this.hasPrioritySupport = true;
                this.hasAdvancedAnalytics = true;
                break;
        }
    }
}
