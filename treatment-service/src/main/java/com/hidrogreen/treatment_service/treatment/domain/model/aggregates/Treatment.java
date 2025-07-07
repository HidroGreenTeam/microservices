package com.hidrogreen.treatment_service.treatment.domain.model.aggregates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hidrogreen.treatment_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.model.events.TreatmentCreatedEvent;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentFrequency;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Treatment aggregate root - Representa un tratamiento completo con sus pasos
 */
@Entity
@Table(name = "treatments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Treatment extends AuditableAbstractAggregateRoot<Treatment> {

    @Column(name = "diagnosis_id", unique = true, nullable = false)
    private Long diagnosisId;

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "disease_type", nullable = false)
    private String diseaseType;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Embedded
    private TreatmentStatus status;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "diagnosis_date")
    private LocalDateTime diagnosisDate;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Embedded
    private TreatmentFrequency frequency;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatmentStep> steps = new ArrayList<>();

    public Treatment(Long diagnosisId, Long cropId, Long profileId, String diseaseType, 
                    Double confidence, String imageUrl, LocalDateTime diagnosisDate) {
        this.diagnosisId = diagnosisId;
        this.cropId = cropId;
        this.profileId = profileId;
        this.diseaseType = diseaseType;
        this.confidence = confidence;
        this.imageUrl = imageUrl;
        this.diagnosisDate = diagnosisDate;
        this.status = new TreatmentStatus();
        
        this.title = String.format("Tratamiento para %s", diseaseType);
        this.description = String.format("Tratamiento generado automáticamente para %s detectada con %.1f%% de confianza", 
                                        diseaseType, confidence * 100);
        this.notes = "Tratamiento creado automáticamente. Agregue pasos según sea necesario.";
        
        this.frequency = new TreatmentFrequency();
        this.startDate = LocalDateTime.now();
        this.endDate = null;
        
        this.registerEvent(new TreatmentCreatedEvent(this));
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return !steps.isEmpty() && steps.stream()
            .allMatch(step -> step.getStatus().isCompleted() || step.getStatus().isSkipped());
    }

    public boolean hasSteps() {
        return !steps.isEmpty();
    }

    public long getPendingStepsCount() {
        return steps.stream()
                .filter(step -> step.getStatus().isPending())
                .count();
    }

    public long getCompletedStepsCount() {
        return steps.stream()
                .filter(step -> step.getStatus().isCompleted())
                .count();
    }

    public double getProgressPercentage() {
        if (steps.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedStepsCount() / steps.size() * 100;
    }

    public void addStep(TreatmentStep step) {
        steps.add(step);
        if (status.isPending()) {
            this.status = new TreatmentStatus(TreatmentStatus.Status.IN_PROGRESS);
        }
    }

    public void removeStep(TreatmentStep step) {
        steps.remove(step);
        if (steps.isEmpty()) {
            this.status = new TreatmentStatus(TreatmentStatus.Status.PENDING);
        }
    }

    public void start() {
        if (!status.isPending()) {
            throw new IllegalStateException("Treatment can only be started when pending");
        }
        this.status = new TreatmentStatus(TreatmentStatus.Status.IN_PROGRESS);
    }

    public void complete() {
        if (!status.isInProgress()) {
            throw new IllegalStateException("Treatment can only be completed when in progress");
        }
        if (steps.stream().anyMatch(step -> !step.getStatus().isCompleted() && !step.getStatus().isSkipped())) {
            throw new IllegalStateException("Cannot complete treatment with pending steps");
        }
        
        this.endDate = LocalDateTime.now();
        this.status = new TreatmentStatus(TreatmentStatus.Status.COMPLETED);
    }

    public void cancel() {
        if (status.isCompleted() || status.isCancelled()) {
            throw new IllegalStateException("Treatment cannot be cancelled when completed or already cancelled");
        }
        this.status = new TreatmentStatus(TreatmentStatus.Status.CANCELLED);
        this.endDate = LocalDateTime.now();
    }
 
}
