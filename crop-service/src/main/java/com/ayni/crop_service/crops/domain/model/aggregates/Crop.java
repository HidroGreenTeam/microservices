package com.ayni.crop_service.crops.domain.model.aggregates;

import java.time.LocalDate;

import com.ayni.crop_service.crops.domain.model.events.CropCreatedEvent; 
import com.ayni.crop_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Crop aggregate root
 */
@Entity
@Table(name = "crops")
@Getter
public class Crop extends AuditableAbstractAggregateRoot<Crop> {

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "area")
    private Double area;

    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @Column(name = "location")
    private String location;

    @Column(name = "image_url")
    private String imageUrl;
 

    protected Crop() {}

    public Crop(Long farmerId, String cropName, Double area, LocalDate plantingDate, String location) {
        this.farmerId = farmerId;
        this.cropName = cropName;
        this.area = area;
        this.plantingDate = plantingDate;
        this.location = location;
        
        this.registerEvent(new CropCreatedEvent(this));
    }

    public Long getCropId() {
        return this.getId();
    }

    public void updateCropName(String cropName) {
        this.cropName = cropName;
    }

    public void updateArea(Double area) {
        this.area = area;
    }

    public void updatePlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void removeImage() {
        this.imageUrl = null;
    }
}
