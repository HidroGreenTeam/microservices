package com.hidrogreen.treatment_activity_service.interfaces.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityRequest {
    
    @NotBlank(message = "El nombre de la actividad es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "La fecha y hora programada es obligatoria")
    private LocalDateTime scheduledDateTime;
    
    @Positive(message = "Los minutos de recordatorio deben ser positivos")
    private Integer reminderMinutesBefore = 30; // Por defecto 30 minutos
}
