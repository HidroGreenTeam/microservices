package com.ayni.crop_service.crops.domain.model.aggregates;

import com.ayni.crop_service.crops.domain.model.valueobjects.*;
import com.ayni.crop_service.crops.domain.model.events.DiagnosisCompletedEvent;
import com.ayni.crop_service.crops.domain.model.events.DiagnosisStartedEvent;
import com.ayni.crop_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Diagnosis aggregate root
 */
@Entity
public class Diagnosis extends AuditableAbstractAggregateRoot<Diagnosis> {

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Embedded
    private DetectionResult detectionResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DiagnosisStatus status;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    protected Diagnosis() {}

    public enum DiagnosisStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    public Diagnosis(Long cropId, Long profileId, String imageUrl) {
        this.cropId = cropId;
        this.profileId = profileId;
        this.imageUrl = imageUrl;
        this.status = DiagnosisStatus.PENDING;
        
        this.registerEvent(new DiagnosisStartedEvent(this));
    }

    public void completeWithResult(DetectionResult detectionResult, Double confidenceScore) {
        this.detectionResult = detectionResult;
        this.confidenceScore = confidenceScore;
        this.status = DiagnosisStatus.COMPLETED;
        this.analyzedAt = LocalDateTime.now();
        
        this.registerEvent(new DiagnosisCompletedEvent(this));
    }

    public void markAsFailed(String errorMessage) {
        this.status = DiagnosisStatus.FAILED;
        this.notes = errorMessage;
        this.analyzedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == DiagnosisStatus.COMPLETED;
    }

    public boolean hasDiseaseDetected() {
        return detectionResult != null && detectionResult.isDiseaseDetected();
    }

    // Getters
    public Long getCropId() {
        return cropId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public DetectionResult getDetectionResult() {
        return detectionResult;
    }

    public DiagnosisStatus getStatus() {
        return status;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public String getNotes() {
        return notes;
    }
}
