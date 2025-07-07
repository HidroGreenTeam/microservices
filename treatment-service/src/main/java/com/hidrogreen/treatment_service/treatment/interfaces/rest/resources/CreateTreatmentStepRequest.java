package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTreatmentStepRequest(
    @NotBlank(message = "Step name is required")
    String name,

    @NotBlank(message = "Step description is required")
    String description,

    @NotNull(message = "Scheduled date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime scheduledDate,

    boolean hasReminder,

    Integer reminderMinutesBefore
) {} 