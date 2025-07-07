package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Treatment Response
 */
public class TreatmentResponse {
    private Long id;
    private Long diagnosisId;
    private Long cropId;
    private Long profileId;
    private String title;
    private String description;
    private String diseaseType;
    private Double confidence;
    private String status;
    private String severity;
    private String imageUrl;
    private LocalDateTime diagnosisDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Contadores de actividades para barra de progreso
    private Integer activitiesCount;
    private Long pendingActivitiesCount;
    private Long completedActivitiesCount;
    private Double progressPercentage; // Porcentaje de progreso

    // Constructors
    public TreatmentResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(Long diagnosisId) { this.diagnosisId = diagnosisId; }

    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDiseaseType() { return diseaseType; }
    public void setDiseaseType(String diseaseType) { this.diseaseType = diseaseType; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(LocalDateTime diagnosisDate) { this.diagnosisDate = diagnosisDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getActivitiesCount() { return activitiesCount; }
    public void setActivitiesCount(Integer activitiesCount) { this.activitiesCount = activitiesCount; }

    public Long getPendingActivitiesCount() { return pendingActivitiesCount; }
    public void setPendingActivitiesCount(Long pendingActivitiesCount) { this.pendingActivitiesCount = pendingActivitiesCount; }

    public Long getCompletedActivitiesCount() { return completedActivitiesCount; }
    public void setCompletedActivitiesCount(Long completedActivitiesCount) { this.completedActivitiesCount = completedActivitiesCount; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
}
