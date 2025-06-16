package com.hidrogreen.treatment_activity_service.domain.model.aggregates;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hidrogreen.treatment_activity_service.domain.model.entities.Activity;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.TreatmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treatments")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Treatment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatmentStatus status = TreatmentStatus.ACTIVE;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "duration_days")
    private Integer durationDays;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
      @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Activity> activities = new ArrayList<>();
    
    public Treatment(String name, String description, Long userId, Integer durationDays) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.durationDays = durationDays;
        this.status = TreatmentStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        if (durationDays != null) {
            this.endDate = this.startDate.plusDays(durationDays);
        }
    }
    
    public void addActivity(Activity activity) {
        activities.add(activity);
        activity.setTreatment(this);
    }
    
    public void completeAt(LocalDateTime completionDate) {
        this.status = TreatmentStatus.COMPLETED;
        this.endDate = completionDate;
    }
    
    public void pause() {
        this.status = TreatmentStatus.PAUSED;
    }
    
    public void resume() {
        this.status = TreatmentStatus.ACTIVE;
    }
    
    public void cancel() {
        this.status = TreatmentStatus.CANCELLED;
        this.endDate = LocalDateTime.now();
    }
}
