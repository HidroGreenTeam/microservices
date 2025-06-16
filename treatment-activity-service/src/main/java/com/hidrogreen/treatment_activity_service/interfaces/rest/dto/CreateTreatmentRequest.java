package com.hidrogreen.treatment_activity_service.interfaces.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreatmentRequest {
    
    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Long userId;
    
    @Positive(message = "La duración debe ser positiva")
    private Integer durationDays;
}
