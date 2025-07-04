package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import java.time.LocalDateTime;


public record ActivityResponse(
    Long id,
    String name,
    String description,
    String type,
    String status,
    Long cropId,
    LocalDateTime scheduledDate,
    String priority,
    String notes
) {}
