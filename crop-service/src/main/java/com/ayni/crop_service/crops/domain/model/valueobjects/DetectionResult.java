package com.ayni.crop_service.crops.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * DetectionResult value object - Del DETECTION-SERVICE
 */
@Embeddable
public record DetectionResult(
    @Column(name = "predicted_class") String predictedClass,
    @Column(name = "confidence") Double confidence,
    @Column(name = "disease_detected") Boolean diseaseDetected,
    @Enumerated(EnumType.STRING)
    @Column(name = "disease_category") DiseaseCategory category,
    @Enumerated(EnumType.STRING) 
    @Column(name = "disease_severity") DiseaseSeverity severity,
    @Column(name = "recommendations", length = 1000) String recommendations
) {
    
    // Compact constructor for validation and normalization
    public DetectionResult {
        if (predictedClass == null || predictedClass.isBlank()) {
            predictedClass = "unknown";
        }
        if (confidence == null || confidence < 0.0 || confidence > 1.0) {
            confidence = 0.0;
        }
        if (diseaseDetected == null) {
            diseaseDetected = !isHealthyClass(predictedClass);
        }
        if (category == null) {
            category = DiseaseCategory.UNKNOWN;
        }
        if (severity == null) {
            severity = DiseaseSeverity.UNKNOWN;
        }
        if (recommendations == null) {
            recommendations = "";
        }
    }
    
    // Default constructor
    public DetectionResult() {
        this("nodisease", 0.0, false, DiseaseCategory.HEALTHY, DiseaseSeverity.UNKNOWN, "");
    }

    // Constructor para compatibilidad (String, boolean, String)
    public DetectionResult(String diseaseName, boolean diseaseDetected, String recommendations) {
        this(diseaseName, 
             diseaseDetected ? 0.95 : 0.98, 
             diseaseDetected, 
             diseaseDetected ? DiseaseCategory.UNKNOWN : DiseaseCategory.HEALTHY,
             diseaseDetected ? DiseaseSeverity.MEDIUM : DiseaseSeverity.UNKNOWN,
             recommendations != null ? recommendations : "");
    }
    
    // Constructor simplificado (String, Double)
    public DetectionResult(String predictedClass, Double confidence) {
        this(predictedClass, 
             confidence, 
             !isHealthyClass(predictedClass), 
             DiseaseCategory.UNKNOWN,
             DiseaseSeverity.UNKNOWN,
             "");
    }
    
    private static boolean isHealthyClass(String predictedClass) {
        return predictedClass != null && 
               ("nodisease".equalsIgnoreCase(predictedClass) || 
                "healthy".equalsIgnoreCase(predictedClass) ||
                "normal".equalsIgnoreCase(predictedClass));
    }

    /**
     * Regla de negocio: Solo confianza > 90% es aceptable para confirmar enfermedad
     */
    public boolean isAcceptableConfidence() {
        return confidence > 0.90;
    }
    
    public boolean isHighConfidence() {
        return confidence >= 0.90;
    }
    
    public boolean isHealthy() {
        return isHealthyClass(predictedClass);
    }

    public boolean isDiseaseDetected() {
        return diseaseDetected != null ? diseaseDetected : !isHealthy();
    }

    public boolean hasDisease() {
        return isDiseaseDetected();
    }

    public boolean isValidResult() {
        return predictedClass != null && !predictedClass.isBlank() && confidence != null;
    }

    /**
     * Solo requiere tratamiento si tiene enfermedad Y confianza > 90%
     */
    public boolean requiresTreatment() {
        return isDiseaseDetected() && isAcceptableConfidence();
    }

    // Getter methods for compatibility
    public String getDiseaseName() {
        return predictedClass;
    }

    public Boolean getDiseaseDetected() {
        return diseaseDetected;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public enum DiseaseCategory {
        FUNGAL, PEST, VIRAL, BACTERIAL, NUTRITIONAL, HEALTHY, UNKNOWN
    }

    public enum DiseaseSeverity {
        LOW, MEDIUM, HIGH, CRITICAL, UNKNOWN
    }
}
