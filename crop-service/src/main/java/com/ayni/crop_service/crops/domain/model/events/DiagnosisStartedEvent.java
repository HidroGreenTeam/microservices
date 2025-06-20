package com.ayni.crop_service.crops.domain.model.events;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.shared.domain.model.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event fired when a diagnosis is started
 */
@Getter
public final class DiagnosisStartedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long diagnosisId;
    private final Long cropId;
    private final Long profileId;
    private final String imageUrl;

    public DiagnosisStartedEvent(Diagnosis diagnosis) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.diagnosisId = diagnosis.getId();
        this.cropId = diagnosis.getCropId();
        this.profileId = diagnosis.getProfileId();
        this.imageUrl = diagnosis.getImageUrl();
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
