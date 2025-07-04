package com.ayni.notification_service.notifications.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double price;
    
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("event_time")
    private LocalDateTime eventTime;
}
