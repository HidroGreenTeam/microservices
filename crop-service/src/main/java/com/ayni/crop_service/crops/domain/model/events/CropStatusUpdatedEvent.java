package com.ayni.crop_service.crops.domain.model.events;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.valueobjects.CropHealthStatus;
import com.ayni.crop_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when a crop health status is updated
 */
@Getter
public final class CropStatusUpdatedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long cropId;
    private final Long profileId;
    private final String oldHealthStatus;
    private final String newHealthStatus;

    public CropStatusUpdatedEvent(Crop crop, CropHealthStatus oldStatus, CropHealthStatus newStatus) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.cropId = crop.getCropId();
        this.profileId = crop.getProfileId();
        this.oldHealthStatus = oldStatus.name();
        this.newHealthStatus = newStatus.name();
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
