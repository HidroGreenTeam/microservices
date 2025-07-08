package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTreatmentStepRequest(
    @NotBlank(message = "Step name is required")
    String name,

    @NotBlank(message = "Step description is required")
    String description,

    @NotNull(message = "Scheduled date is required")
    LocalDateTime scheduledDate,

    boolean hasReminder,

    Integer reminderMinutesBefore
) {} 