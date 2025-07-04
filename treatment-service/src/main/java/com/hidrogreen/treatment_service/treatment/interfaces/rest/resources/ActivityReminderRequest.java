package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import jakarta.validation.constraints.*;


public record ActivityReminderRequest(
    @NotBlank(message = "Activity name is required")
    String activityName,
    
    @NotBlank(message = "Crop name is required")
    String cropName,
    
    boolean sendEmail,
    boolean sendWhatsApp,
    
    @Email(message = "Invalid email format")
    String email,
    
    String phone
) {}
