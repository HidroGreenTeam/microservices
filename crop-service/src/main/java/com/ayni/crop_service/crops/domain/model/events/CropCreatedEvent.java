package com.ayni.crop_service.crops.domain.model.events;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when a crop is created
 */
@Getter
public final class CropCreatedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long cropId;
    private final Long profileId;
    private final String cropName;

    public CropCreatedEvent(Crop crop) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.cropId = crop.getCropId();
        this.profileId = crop.getProfileId();
        this.cropName = crop.getCropName();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
