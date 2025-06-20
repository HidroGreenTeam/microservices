package com.ayni.crop_service.crops.domain.model.aggregates;

import com.ayni.crop_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

import java.util.List;

/**
 * Disease aggregate root
 */
@Entity
public class Disease extends AuditableAbstractAggregateRoot<Disease> {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "symptoms", length = 2000)
    private String symptoms;

    @Column(name = "causes", length = 1000)
    private String causes;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private DiseaseSeverity severity;    @ElementCollection
    @CollectionTable(name = "disease_affected_crops", joinColumns = @JoinColumn(name = "disease_id"))
    @Column(name = "crop_name")
    private List<String> affectedCropNames;

    @Column(name = "prevention_measures", length = 2000)
    private String preventionMeasures;

    @Column(name = "treatment_recommendations", length = 2000)
    private String treatmentRecommendations;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected Disease() {}    public Disease(String name, String scientificName, String description, 
                   DiseaseSeverity severity, List<String> affectedCropNames) {
        this.name = name;
        this.scientificName = scientificName;
        this.description = description;
        this.severity = severity;
        this.affectedCropNames = affectedCropNames;
        this.isActive = true;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void updateCauses(String causes) {
        this.causes = causes;
    }

    public void updatePreventionMeasures(String preventionMeasures) {
        this.preventionMeasures = preventionMeasures;
    }

    public void updateTreatmentRecommendations(String treatmentRecommendations) {
        this.treatmentRecommendations = treatmentRecommendations;
    }

    public void updateSeverity(DiseaseSeverity severity) {
        this.severity = severity;
    }    public void addAffectedCropName(String cropName) {
        if (!this.affectedCropNames.contains(cropName)) {
            this.affectedCropNames.add(cropName);
        }
    }

    public void removeAffectedCropName(String cropName) {
        this.affectedCropNames.remove(cropName);
    }

    public void deactivate() {
        this.isActive = false;
    }    public void activate() {
        this.isActive = true;
    }

    public Long getDiseaseId() {
        return this.getId();
    }

    public String getName() {
        return name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getDescription() {
        return description;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getCauses() {
        return causes;
    }

    public DiseaseSeverity getSeverity() {
        return severity;
    }

    public List<String> getAffectedCropNames() {
        return affectedCropNames;
    }

    public String getPreventionMeasures() {
        return preventionMeasures;
    }

    public String getTreatmentRecommendations() {
        return treatmentRecommendations;
    }

    public boolean isActive() {
        return isActive;
    }

    public enum DiseaseSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
