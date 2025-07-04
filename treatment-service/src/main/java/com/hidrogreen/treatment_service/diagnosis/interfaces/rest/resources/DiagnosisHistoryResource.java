package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources;


public record DiagnosisHistoryResource(
    Long id,
    String fecha,
    String cropName,
    Long cropId,
    String predicted_class,
    String estado,
    Double confianza,
    String imagenUrl
) {} 