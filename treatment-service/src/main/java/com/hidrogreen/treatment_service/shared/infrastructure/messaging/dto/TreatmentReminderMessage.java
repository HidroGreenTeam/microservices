package com.hidrogreen.treatment_service.shared.infrastructure.messaging.dto;

import java.time.LocalDateTime;

public record TreatmentReminderMessage(
    Long stepId,
    Long treatmentId,
    Long profileId,
    String stepName,
    String stepDescription,
    String treatmentTitle,
    String diseaseType,
    LocalDateTime scheduledDate,
    Integer reminderMinutesBefore
) {} 