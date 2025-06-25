package com.hidrogreen.subscription_service.subscriptions.domain.model.aggregates;

import com.hidrogreen.subscription_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hidrogreen.subscription_service.subscriptions.domain.model.entities.SubscriptionPlan;
import com.hidrogreen.subscription_service.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {

    @Getter
    @Setter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Getter
    @Setter
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Getter
    @Setter
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Getter
    @Setter
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew;

    @Getter
    @Setter
    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Getter
    @Setter
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Getter
    @Setter
    @Column(name = "cancelled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelledAt;

    public Subscription(Long userId, SubscriptionPlan subscriptionPlan, Boolean autoRenew) {
        this.userId = userId;
        this.subscriptionPlan = subscriptionPlan;
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = new Date();
        this.endDate = calculateEndDate(subscriptionPlan.getDurationDays());
        this.autoRenew = autoRenew;
    }

    public Subscription(Long userId, SubscriptionPlan subscriptionPlan, Boolean autoRenew, String paymentReference) {
        this.userId = userId;
        this.subscriptionPlan = subscriptionPlan;
        this.status = SubscriptionStatus.PENDING_PAYMENT;
        this.startDate = new Date();
        this.endDate = calculateEndDate(subscriptionPlan.getDurationDays());
        this.autoRenew = autoRenew;
        this.paymentReference = paymentReference;
    }

    private Date calculateEndDate(Integer durationDays) {
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(durationDays);
        return java.sql.Timestamp.valueOf(endDateTime);
    }

    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = new Date();
        this.autoRenew = false;
    }

    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.autoRenew = false;
    }

    public void renew(SubscriptionPlan newPlan) {
        this.subscriptionPlan = newPlan;
        this.startDate = new Date();
        this.endDate = calculateEndDate(newPlan.getDurationDays());
        this.status = SubscriptionStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE && 
               this.endDate.after(new Date());
    }

    public boolean isExpired() {
        return this.endDate.before(new Date()) || 
               this.status == SubscriptionStatus.EXPIRED;
    }

    public boolean canRenew() {
        return this.autoRenew && 
               (this.status == SubscriptionStatus.ACTIVE || 
                this.status == SubscriptionStatus.EXPIRED);
    }

    public int getDaysRemaining() {
        if (isExpired()) {
            return 0;
        }
        long diffInMillies = this.endDate.getTime() - new Date().getTime();
        return (int) (diffInMillies / (1000 * 60 * 60 * 24));
    }
}
