package com.hidrogreen.treatment_service.treatment.domain.model.entities;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class ActivityNote extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "note_content", nullable = false, length = 2000)
    private String content;

    @Column(name = "note_type")
    private String noteType;

    @Column(name = "author")
    private String author;

    @Column(name = "is_important", nullable = false)
    private boolean isImportant = false;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    protected ActivityNote() {}

    public ActivityNote(Activity activity, String content, String author, String noteType) {
        this.activity = activity;
        this.content = content;
        this.author = author;
        this.noteType = noteType;
    }

    
    public String getContent() {
        return this.content;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be empty");
        }
        this.content = newContent;
    }

    public void markAsImportant() {
        this.isImportant = true;
    }

    public void markAsNormal() {
        this.isImportant = false;
    }

    public void markAsPrivate() {
        this.isPrivate = true;
    }

    public void markAsPublic() {
        this.isPrivate = false;
    }

    public void updateType(String noteType) {
        this.noteType = noteType;
    }

    public boolean isObservation() {
        return "OBSERVATION".equals(noteType);
    }

    public boolean isReminder() {
        return "REMINDER".equals(noteType);
    }

    public boolean isWarning() {
        return "WARNING".equals(noteType);
    }
}
