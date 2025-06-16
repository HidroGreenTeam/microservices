package com.hidrogreen.treatment_activity_service.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReminderEvent {
    private Long activityId;
    private Long userId;
    private String activityName;
    private String activityDescription;
    private LocalDateTime scheduledDateTime;
    private String treatmentName;
    private String notificationType = "ACTIVITY_REMINDER";
}
