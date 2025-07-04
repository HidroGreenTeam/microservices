package com.ayni.crop_service.crops.domain.model.events;

import lombok.Getter;

@Getter
public class CropsCreatedEvent {
    private final String cropName;
    private final Long farmerId;
    private final String email;

    public CropsCreatedEvent(String cropName, Long farmerId, String email) {
        this.cropName = cropName;
        this.farmerId = farmerId;
        this.email = email;
    }
}