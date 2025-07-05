package com.hidrogreen.treatment_service.treatment.domain.model.aggregates;

import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.*;
import com.hidrogreen.treatment_service.treatment.domain.model.events.TreatmentActivityCreatedEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("TREATMENT")
@Getter
@Setter
public class TreatmentActivity extends Activity {

    @Column(name = "treatment_id")
    private Long treatmentId;

    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @Column(name = "target_disease")
    private String targetDisease;

    @Column(name = "treatment_step_order")
    private Integer treatmentStepOrder;

    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;

    @Column(name = "can_be_detached", nullable = false)
    private boolean canBeDetached = false;

    @Column(name = "treatment_phase")
    private String treatmentPhase;

    protected TreatmentActivity() {}

    public TreatmentActivity(Long cropId, Long treatmentId, String title, 
                           String description, String activityType, 
                           LocalDateTime scheduledAt, String frequency,
                           Integer treatmentStepOrder, boolean isMandatory,
                           String treatmentPhase) {
        super(cropId, title, description, 
              new ActivityType(ActivityType.Type.valueOf(activityType)), 
              new ActivityOrigin(ActivityOrigin.Origin.TREATMENT_BASED), 
              scheduledAt, 
              new ActivityFrequency(ActivityFrequency.Frequency.valueOf(frequency)));
        this.treatmentId = treatmentId;
        this.treatmentStepOrder = treatmentStepOrder;
        this.isMandatory = isMandatory;
        this.canBeDetached = !isMandatory;
        this.treatmentPhase = treatmentPhase;
        
        this.registerEvent(new TreatmentActivityCreatedEvent(this));
    }

    public TreatmentActivity(Long cropId, Long diagnosisId, String title, String description,
                           String targetDisease, LocalDateTime scheduledAt, String treatmentPhase) {
        super(cropId, title, description,
              new ActivityType(ActivityType.Type.SPRAYING),
              new ActivityOrigin(ActivityOrigin.Origin.TREATMENT_BASED),
              scheduledAt,
              new ActivityFrequency(ActivityFrequency.Frequency.ONCE));
        
        this.diagnosisId = diagnosisId;
        this.targetDisease = targetDisease;
        this.treatmentStepOrder = 1;
        this.isMandatory = true;
        this.canBeDetached = false;
        this.treatmentPhase = treatmentPhase;
        
        this.registerEvent(new TreatmentActivityCreatedEvent(this));
    }

    public TreatmentActivity(Long cropId, Long treatmentId, String title, String description,
                           ActivityType activityType, ActivityOrigin origin,
                           LocalDateTime scheduledAt, ActivityFrequency frequency,
                           Integer treatmentStepOrder, Boolean isMandatory,
                           String treatmentPhase) {
        super(cropId, title, description, activityType, origin, scheduledAt, frequency);
        this.treatmentId = treatmentId;
        this.treatmentStepOrder = treatmentStepOrder;
        this.isMandatory = isMandatory;
        this.treatmentPhase = treatmentPhase;
    }

    public void updateTreatmentPhase(String phase) {
        this.treatmentPhase = phase;
    }

    public boolean isLinkedToTreatment() {
        return treatmentId != null && treatmentId > 0;
    }

    @Override
    public void complete() {
        super.complete();
        if (isLinkedToTreatment() || diagnosisId != null) {
        }
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public Integer getTreatmentStepOrder() {
        return treatmentStepOrder;
    }

    public boolean isMandatory() {
        return isMandatory;
    }
}

