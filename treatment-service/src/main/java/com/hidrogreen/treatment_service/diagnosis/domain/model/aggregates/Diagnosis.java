package com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates;

import com.hidrogreen.treatment_service.diagnosis.domain.model.valueobjects.*;
import com.hidrogreen.treatment_service.diagnosis.domain.model.events.DiagnosisCompletedEvent;
import com.hidrogreen.treatment_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Diagnosis aggregate root
 */
@Entity
@Getter
@Setter
public class Diagnosis extends AuditableAbstractAggregateRoot<Diagnosis> {

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

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

    public Diagnosis(Long cropId, String imageUrl) {
        this.cropId = cropId;
        this.imageUrl = imageUrl;
        this.status = DiagnosisStatus.PENDING;
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

    // Custom getters for business logic
    public Long getCropId() {
        return cropId;
    }

    // Additional getter methods for compatibility
    public String getImageUrl() {
        return imageUrl;
    }

    public DiagnosisStatus getStatus() {
        return status;
    }

    public DetectionResult getDetectionResult() {
        return detectionResult;
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