package com.ayni.crop_service.crops.domain.model.events;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when a diagnosis is completed
 */
public final class DiagnosisCompletedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long diagnosisId;
    private final Long cropId;
    private final Long profileId;
    private final boolean diseaseDetected;
    private final String detectedDisease;
    private final Double confidenceScore;

    public DiagnosisCompletedEvent(Diagnosis diagnosis) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.diagnosisId = diagnosis.getId();
        this.cropId = diagnosis.getCropId();
        this.profileId = diagnosis.getProfileId();
        this.diseaseDetected = diagnosis.hasDiseaseDetected();
        this.detectedDisease = diagnosis.getDetectionResult() != null ? 
            diagnosis.getDetectionResult().getDiseaseName() : null;
        this.confidenceScore = diagnosis.getConfidenceScore();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "DiagnosisCompletedEvent";
    }

    public Long getDiagnosisId() {
        return diagnosisId;
    }

    public Long getCropId() {
        return cropId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public boolean isDiseaseDetected() {
        return diseaseDetected;
    }

    public String getDetectedDisease() {
        return detectedDisease;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }
}
