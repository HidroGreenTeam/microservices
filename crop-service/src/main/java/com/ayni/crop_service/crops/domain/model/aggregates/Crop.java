package com.ayni.crop_service.crops.domain.model.aggregates;

import com.ayni.crop_service.crops.domain.model.valueobjects.*;
import com.ayni.crop_service.crops.domain.model.events.CropCreatedEvent;
import com.ayni.crop_service.crops.domain.model.events.CropStatusUpdatedEvent;
import com.ayni.crop_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Crop aggregate root
 */
@Entity
@Table(name = "crops")
public class Crop extends AuditableAbstractAggregateRoot<Crop> {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false)
    private CropHealthStatus healthStatus;

    @Column(name = "notes", length = 1000)
    private String notes;

    protected Crop() {}

    public Crop(Long profileId, String cropName, LocalDate plantingDate, String location) {
        this.profileId = profileId;
        this.cropName = cropName;
        this.plantingDate = plantingDate;
        this.location = location;
        this.healthStatus = CropHealthStatus.HEALTHY;
        
        this.registerEvent(new CropCreatedEvent(this));
    }

    public void updateHealthStatus(CropHealthStatus newStatus) {
        CropHealthStatus oldStatus = this.healthStatus;
        this.healthStatus = newStatus;
        this.registerEvent(new CropStatusUpdatedEvent(this, oldStatus, newStatus));
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }

    public Long getCropId() {
        return this.getId();
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getCropName() {
        return cropName;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public String getLocation() {
        return location;
    }

    public CropHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isHealthy() {
        return healthStatus.isHealthy();
    }

    public boolean isDiseased() {
        return healthStatus.isDiseased();
    }

    public boolean isAtRisk() {
        return healthStatus.isAtRisk();
    }

    public boolean isCritical() {
        return healthStatus.isCritical();
    }

    public int getHealthScore() {
        return healthStatus.getHealthScore();
    }
}
