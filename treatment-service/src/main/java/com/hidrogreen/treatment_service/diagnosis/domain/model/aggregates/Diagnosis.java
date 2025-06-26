package com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates;

import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CreateDiagnosisCommand;
import com.hidrogreen.treatment_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Diagnosis extends AuditableAbstractAggregateRoot<Diagnosis> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Crop ID is required")
    @Column(name = "crop_id")
    private Long cropId;

    @NotNull(message = "Farmer ID is required")
    @Column(name = "farmer_id")
    private Long farmerId;

    @NotBlank(message = "Predicted class is required")
    @Column(name = "predicted_class")
    private String predictedClass;

    @NotNull(message = "Confidence is required")
    @Column(name = "confidence")
    private Double confidence;

    @NotNull(message = "Disease detected flag is required")
    @Column(name = "disease_detected")
    private Boolean diseaseDetected;

    @NotNull(message = "Requires treatment flag is required")
    @Column(name = "requires_treatment")
    private Boolean requiresTreatment;

    @Column(name = "image_filename")
    private String imageFilename;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_public_id")
    private String imagePublicId;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "detection_notes", columnDefinition = "TEXT")
    private String detectionNotes;

    @NotNull(message = "Detection date is required")
    @Column(name = "detection_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime detectionDate;

    // Constructor desde command
    public Diagnosis(CreateDiagnosisCommand command) {
        super();
        this.cropId = command.cropId();
        this.farmerId = command.farmerId();
        this.predictedClass = command.predictedClass();
        this.confidence = command.confidence();
        this.diseaseDetected = command.diseaseDetected();
        this.requiresTreatment = command.requiresTreatment();
        this.imageFilename = command.imageFilename();
        this.imageUrl = command.imageUrl();
        this.detectionNotes = command.detectionNotes();
        this.detectionDate = command.detectionDate() != null ? command.detectionDate() : LocalDateTime.now();
    }

    // Método de actualización
    public Diagnosis update(
            String predictedClass,
            Double confidence,
            Boolean diseaseDetected,
            Boolean requiresTreatment,
            String detectionNotes
    ) {
        this.predictedClass = predictedClass;
        this.confidence = confidence;
        this.diseaseDetected = diseaseDetected;
        this.requiresTreatment = requiresTreatment;
        this.detectionNotes = detectionNotes;
        return this;
    }

    // Business methods
    public boolean isHealthy() {
        return !diseaseDetected;
    }

    public boolean needsImmediateTreatment() {
        return diseaseDetected && requiresTreatment && confidence > 0.8;
    }

    public String getSeverityLevel() {
        if (!diseaseDetected) return "HEALTHY";
        if (confidence > 0.9) return "HIGH";
        if (confidence > 0.7) return "MEDIUM";
        return "LOW";
    }
}
