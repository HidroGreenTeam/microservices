package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.transform;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources.DiagnosisHistoryResource;


public class DiagnosisHistoryResourceFromEntityAssembler {

    public static DiagnosisHistoryResource toResourceFromEntity(Diagnosis diagnosis, String cropName) {
        
        String estado = mapStatusToSpanish(diagnosis.getStatus().name(), diagnosis.hasDiseaseDetected());
        
        return new DiagnosisHistoryResource(
            diagnosis.getId(),
            diagnosis.getAnalyzedAt() != null ? diagnosis.getAnalyzedAt().toString() : diagnosis.getCreatedAt().toString(),
            cropName,
            diagnosis.getCropId(),
            diagnosis.getDetectionResult() != null ? diagnosis.getDetectionResult().getDiseaseName() : "Sin analizar",
            estado,
            diagnosis.getConfidenceScore(),
            diagnosis.getImageUrl()
        );
    }

    private static String mapStatusToSpanish(String status, boolean diseaseDetected) {
        return switch (status) {
            case "PENDING" -> "pendiente";
            case "PROCESSING" -> "procesando";
            case "COMPLETED" -> diseaseDetected ? "en_tratamiento" : "saludable";
            case "FAILED" -> "fallido";
            default -> "desconocido";
        };
    }
} 