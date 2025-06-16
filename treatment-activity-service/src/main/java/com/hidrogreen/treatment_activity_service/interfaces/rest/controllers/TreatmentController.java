package com.hidrogreen.treatment_activity_service.interfaces.rest.controllers;

import com.hidrogreen.treatment_activity_service.application.services.TreatmentService;
import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.interfaces.rest.dto.CreateTreatmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/treatments")
@Tag(name = "Treatments", description = "Treatment management operations")
public class TreatmentController {
    
    private final TreatmentService treatmentService;
    
    @Autowired
    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new treatment")
    public ResponseEntity<Treatment> createTreatment(@Valid @RequestBody CreateTreatmentRequest request) {
        try {
            Treatment treatment = treatmentService.createTreatment(
                request.getName(),
                request.getDescription(),
                request.getUserId(),
                request.getDurationDays()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(treatment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all treatments for a user")
    public ResponseEntity<List<Treatment>> getTreatmentsByUserId(@PathVariable Long userId) {
        List<Treatment> treatments = treatmentService.getTreatmentsByUserId(userId);
        return ResponseEntity.ok(treatments);
    }
    
    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active treatments for a user")
    public ResponseEntity<List<Treatment>> getActiveTreatmentsByUserId(@PathVariable Long userId) {
        List<Treatment> treatments = treatmentService.getActiveTreatmentsByUserId(userId);
        return ResponseEntity.ok(treatments);
    }
    
    @GetMapping("/{treatmentId}")
    @Operation(summary = "Get treatment by ID")
    public ResponseEntity<Treatment> getTreatmentById(@PathVariable Long treatmentId) {
        return treatmentService.getTreatmentById(treatmentId)
                .map(treatment -> ResponseEntity.ok(treatment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{treatmentId}")
    @Operation(summary = "Update treatment")
    public ResponseEntity<Treatment> updateTreatment(
            @PathVariable Long treatmentId,
            @Valid @RequestBody CreateTreatmentRequest request) {
        try {
            Treatment treatment = treatmentService.updateTreatment(
                treatmentId,
                request.getName(),
                request.getDescription(),
                request.getDurationDays()
            );
            return ResponseEntity.ok(treatment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{treatmentId}/complete")
    @Operation(summary = "Complete treatment")
    public ResponseEntity<Void> completeTreatment(@PathVariable Long treatmentId) {
        try {
            treatmentService.completeTreatment(treatmentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{treatmentId}/pause")
    @Operation(summary = "Pause treatment")
    public ResponseEntity<Void> pauseTreatment(@PathVariable Long treatmentId) {
        try {
            treatmentService.pauseTreatment(treatmentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{treatmentId}/resume")
    @Operation(summary = "Resume treatment")
    public ResponseEntity<Void> resumeTreatment(@PathVariable Long treatmentId) {
        try {
            treatmentService.resumeTreatment(treatmentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{treatmentId}/cancel")
    @Operation(summary = "Cancel treatment")
    public ResponseEntity<Void> cancelTreatment(@PathVariable Long treatmentId) {
        try {
            treatmentService.cancelTreatment(treatmentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{treatmentId}")
    @Operation(summary = "Delete treatment")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Long treatmentId) {
        try {
            treatmentService.deleteTreatment(treatmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
