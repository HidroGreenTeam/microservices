package com.hidrogreen.treatment_service.diagnosis.domain.model.events;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when a diagnosis is completed
 */
@Getter
@Setter
public final class DiagnosisCompletedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long diagnosisId;
    private final Long cropId;
    private final boolean diseaseDetected;
    private final String detectedDisease;
    private final Double confidenceScore;

    public DiagnosisCompletedEvent(Diagnosis diagnosis) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.diagnosisId = diagnosis.getId();
        this.cropId = diagnosis.getCropId();
        this.diseaseDetected = diagnosis.hasDiseaseDetected();
        this.detectedDisease = diagnosis.getDetectionResult() != null ? 
            diagnosis.getDetectionResult().getDiseaseName() : null;
        this.confidenceScore = diagnosis.getConfidenceScore();
    }

    @Override
    public String getEventType() {
        return "DiagnosisCompletedEvent";
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    // Additional getter methods for compatibility
    public boolean isDiseaseDetected() {
        return diseaseDetected;
    }

    public Long getDiagnosisId() {
        return diagnosisId;
    }

    public Long getCropId() {
        return cropId;
    }

    public String getDetectedDisease() {
        return detectedDisease;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }
} 