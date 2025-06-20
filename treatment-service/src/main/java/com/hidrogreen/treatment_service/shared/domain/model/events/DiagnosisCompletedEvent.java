package com.hidrogreen.treatment_service.shared.domain.model.events;

import java.time.LocalDateTime;

/**
 * Diagnosis Completed Event (Stub for external integration)
 * This represents a disease diagnosis completed event from crop-service
 */
public class DiagnosisCompletedEvent implements DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long cropId;
    private final Long diagnosisId;
    private final boolean diseaseDetected;
    private final String detectedDisease;
    private final double confidenceScore;
    
    public DiagnosisCompletedEvent(String eventId, LocalDateTime occurredOn, Long cropId, 
                                   Long diagnosisId, boolean diseaseDetected, 
                                   String detectedDisease, double confidenceScore) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.cropId = cropId;
        this.diagnosisId = diagnosisId;
        this.diseaseDetected = diseaseDetected;
        this.detectedDisease = detectedDisease;
        this.confidenceScore = confidenceScore;
    }
    
    @Override
    public String getEventId() {
        return eventId;
    }
    
    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    public Long getCropId() {
        return cropId;
    }
    
    public Long getDiagnosisId() {
        return diagnosisId;
    }
    
    public boolean isDiseaseDetected() {
        return diseaseDetected;
    }
    
    public String getDetectedDisease() {
        return detectedDisease;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
} 