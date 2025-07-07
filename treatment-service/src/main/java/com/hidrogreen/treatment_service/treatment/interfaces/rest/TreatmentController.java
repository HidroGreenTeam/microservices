package com.hidrogreen.treatment_service.treatment.interfaces.rest;

import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentCommandService;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentQueryService;
import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.exceptions.TreatmentNotFoundException;
import com.hidrogreen.treatment_service.treatment.domain.exceptions.TreatmentStepNotFoundException;
import com.hidrogreen.treatment_service.treatment.interfaces.rest.resources.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;

/**
 * REST Controller for managing Treatments and their Activities
 */
@RestController
@RequestMapping("/api/v1/treatments")
@CrossOrigin(origins = "*")
public class TreatmentController {

    private final TreatmentCommandService commandService;
    private final TreatmentQueryService queryService;

    public TreatmentController(TreatmentCommandService commandService, TreatmentQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/{treatmentId}/steps")
    public ResponseEntity<TreatmentStepResponse> createStep(
            @PathVariable Long treatmentId,
            @Valid @RequestBody CreateTreatmentStepRequest request) {
        Treatment treatment = commandService.addStep(
            treatmentId,
            request.name(),
            request.description(),
            request.scheduledDate(),
            request.hasReminder(),
            request.reminderMinutesBefore()
        );

        TreatmentStep step = treatment.getSteps().get(treatment.getSteps().size() - 1);
        return ResponseEntity.ok(convertToStepResponse(step));
    }

    @GetMapping("/{treatmentId}/steps")
    public ResponseEntity<List<TreatmentStepResponse>> getStepsByTreatmentId(@PathVariable Long treatmentId) {
        Treatment treatment = queryService.getTreatmentById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));

        List<TreatmentStepResponse> responses = treatment.getSteps().stream()
            .map(this::convertToStepResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/steps/{stepId}/complete")
    public ResponseEntity<TreatmentStepResponse> completeStep(@PathVariable Long stepId) {
        Treatment treatment = queryService.getTreatmentByStepId(stepId)
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        TreatmentStep step = treatment.getSteps().stream()
            .filter(s -> s.getId().equals(stepId))
            .findFirst()
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        step.complete();
        treatment = commandService.saveTreatment(treatment);

        return ResponseEntity.ok(convertToStepResponse(step));
    }

    @PatchMapping("/steps/{stepId}/skip")
    public ResponseEntity<TreatmentStepResponse> skipStep(@PathVariable Long stepId) {
        Treatment treatment = queryService.getTreatmentByStepId(stepId)
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        TreatmentStep step = treatment.getSteps().stream()
            .filter(s -> s.getId().equals(stepId))
            .findFirst()
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        step.skip();
        treatment = commandService.saveTreatment(treatment);

        return ResponseEntity.ok(convertToStepResponse(step));
    }

    @PutMapping("/steps/{stepId}")
    public ResponseEntity<TreatmentStepResponse> updateStep(
            @PathVariable Long stepId,
            @Valid @RequestBody CreateTreatmentStepRequest request) {
        Treatment treatment = queryService.getTreatmentByStepId(stepId)
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        TreatmentStep step = treatment.getSteps().stream()
            .filter(s -> s.getId().equals(stepId))
            .findFirst()
            .orElseThrow(() -> new TreatmentStepNotFoundException(stepId));

        step.updateScheduledDate(request.scheduledDate());
        step.updateReminder(request.hasReminder(), request.reminderMinutesBefore());
        treatment = commandService.saveTreatment(treatment);

        return ResponseEntity.ok(convertToStepResponse(step));
    }

    @GetMapping("/{treatmentId}")
    public ResponseEntity<TreatmentResponse> getTreatmentById(@PathVariable Long treatmentId) {
        Treatment treatment = queryService.getTreatmentById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));
        return ResponseEntity.ok(convertToResponse(treatment));
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<TreatmentResponse>> getTreatmentsByProfileId(@PathVariable Long profileId) {
        List<Treatment> treatments = queryService.getTreatmentsByProfileId(profileId);
        List<TreatmentResponse> responses = treatments.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<List<TreatmentResponse>> getTreatmentsByCropId(@PathVariable Long cropId) {
        List<Treatment> treatments = queryService.getTreatmentsByCropId(cropId);
        List<TreatmentResponse> responses = treatments.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TreatmentResponse>> getOverdueTreatments() {
        List<Treatment> treatments = queryService.getOverdueTreatments();
        List<TreatmentResponse> responses = treatments.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/steps/overdue")
    public ResponseEntity<List<TreatmentStepResponse>> getOverdueSteps() {
        List<TreatmentStep> steps = queryService.getOverdueSteps();
        List<TreatmentStepResponse> responses = steps.stream()
            .map(this::convertToStepResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/steps/reminders")
    public ResponseEntity<List<TreatmentStepResponse>> getStepsWithReminders() {
        List<TreatmentStep> steps = queryService.getStepsWithReminders();
        List<TreatmentStepResponse> responses = steps.stream()
            .map(this::convertToStepResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private TreatmentStepResponse convertToStepResponse(TreatmentStep step) {
        TreatmentStepResponse response = new TreatmentStepResponse();
        response.setId(step.getId());
        response.setName(step.getName());
        response.setDescription(step.getDescription());
        response.setScheduledDate(step.getScheduledDate());
        response.setCompletedDate(step.getCompletedDate());
        response.setStatus(step.getStatus().toString());
        response.setHasReminder(step.isHasReminder());
        response.setReminderMinutesBefore(step.getReminderMinutesBefore());
        response.setCreatedAt(step.getCreatedAt());
        response.setUpdatedAt(step.getUpdatedAt());
        return response;
    }

    private TreatmentResponse convertToResponse(Treatment treatment) {
        TreatmentResponse response = new TreatmentResponse();
        response.setId(treatment.getId());
        response.setDiagnosisId(treatment.getDiagnosisId());
        response.setCropId(treatment.getCropId());
        response.setProfileId(treatment.getProfileId());
        response.setTitle(treatment.getTitle());
        response.setDescription(treatment.getDescription());
        response.setDiseaseType(treatment.getDiseaseType());
        response.setConfidence(treatment.getConfidence());
        response.setStatus(treatment.getStatus().toString());
        response.setImageUrl(treatment.getImageUrl());
        response.setDiagnosisDate(treatment.getDiagnosisDate());
        response.setNotes(treatment.getNotes());
        response.setCreatedAt(treatment.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        response.setUpdatedAt(treatment.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        response.setActivitiesCount(treatment.getSteps().size());
        response.setPendingActivitiesCount(treatment.getPendingStepsCount());
        response.setCompletedActivitiesCount(treatment.getCompletedStepsCount());
        response.setProgressPercentage(treatment.getProgressPercentage());
        return response;
    }
}
