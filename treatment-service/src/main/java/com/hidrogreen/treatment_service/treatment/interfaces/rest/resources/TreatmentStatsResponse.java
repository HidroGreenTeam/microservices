package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

/**
 * Treatment Statistics Response
 */
public class TreatmentStatsResponse {
    private Long treatmentId;
    private Integer totalActivities;
    private Long completedActivities;
    private Long pendingActivities;
    private Double progressPercentage;
    private Boolean isCompleted;

    // Constructors
    public TreatmentStatsResponse() {}

    // Getters and Setters
    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }

    public Integer getTotalActivities() { return totalActivities; }
    public void setTotalActivities(Integer totalActivities) { this.totalActivities = totalActivities; }

    public Long getCompletedActivities() { return completedActivities; }
    public void setCompletedActivities(Long completedActivities) { this.completedActivities = completedActivities; }

    public Long getPendingActivities() { return pendingActivities; }
    public void setPendingActivities(Long pendingActivities) { this.pendingActivities = pendingActivities; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
}
