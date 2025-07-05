package com.hidrogreen.treatment_service.treatment.domain.model.aggregates;

import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.*;
import com.hidrogreen.treatment_service.treatment.domain.model.events.StandaloneActivityCreatedEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("STANDALONE")
@Getter
@Setter
public class StandaloneActivity extends Activity {

    @Column(name = "created_by_user", nullable = false)
    private String createdByUser;

    @Column(name = "reminder_enabled", nullable = false)
    private boolean reminderEnabled = false;

    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;

    protected StandaloneActivity() {}

    public StandaloneActivity(Long cropId, String title, String description,
                            ActivityType activityType, LocalDateTime scheduledAt,
                            ActivityFrequency frequency, String createdByUser) {
        super(cropId, title, description, activityType, 
              new ActivityOrigin(ActivityOrigin.Origin.STANDALONE), 
              scheduledAt, frequency);
        this.createdByUser = createdByUser;
        this.reminderEnabled = false; 
        
        this.registerEvent(new StandaloneActivityCreatedEvent(this));
    }

    public void enableReminder() {
        this.reminderEnabled = true;
    }

    public void disableReminder() {
        this.reminderEnabled = false;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
 
    public String getCreatedByUser() {
        return createdByUser;
    }
}
