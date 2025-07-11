package com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionNotificationDto {
    
    @JsonProperty("notification_type")
    private String notificationType;
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("user_email")
    private String userEmail;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("subscription_type")
    private String subscriptionType;
    
    @JsonProperty("subscription_id")
    private Long subscriptionId;
    
    @JsonProperty("plan_name")
    private String planName;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    
    @JsonProperty("event_time")
    private LocalDateTime eventTime;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("features")
    private String features;
    
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    
    @JsonProperty("payment_reference")
    private String paymentReference;
}
