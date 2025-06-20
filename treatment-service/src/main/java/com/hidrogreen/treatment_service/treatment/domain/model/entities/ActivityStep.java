package com.hidrogreen.treatment_service.treatment.domain.model.entities;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Activity Step entity
 */
@Entity
@Getter
public class ActivityStep extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "instructions", length = 2000)
    private String instructions;

    @Column(name = "completion_notes", length = 1000)
    private String completionNotes;

    protected ActivityStep() {}

    public ActivityStep(Activity activity, Integer stepOrder, String title, 
                       String description, boolean isMandatory) {
        this.activity = activity;
        this.stepOrder = stepOrder;
        this.title = title;
        this.description = description;
        this.isMandatory = isMandatory;
    }

    public void complete(String completionNotes) {
        if (this.isCompleted) {
            throw new IllegalStateException("Step is already completed");
        }
        this.isCompleted = true;
        this.completionNotes = completionNotes;
    }

    public void markAsIncomplete() {
        this.isCompleted = false;
        this.completionNotes = null;
    }

    public void updateInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void updateEstimatedTime(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public void updateOrder(Integer newOrder) {
        this.stepOrder = newOrder;
    }

    public boolean canBeSkipped() {
        return !isMandatory;
    }
}
