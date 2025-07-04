package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import jakarta.validation.constraints.Size;


public record CompleteActivityRequest(
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    String notes
) {}
