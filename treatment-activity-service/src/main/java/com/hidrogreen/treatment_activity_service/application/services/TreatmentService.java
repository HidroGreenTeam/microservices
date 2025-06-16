package com.hidrogreen.treatment_activity_service.application.services;

import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.TreatmentStatus;
import com.hidrogreen.treatment_activity_service.infrastructure.persistence.jpa.repositories.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentService {
    
    private final TreatmentRepository treatmentRepository;
    
    @Autowired
    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }
    
    public Treatment createTreatment(String name, String description, Long userId, Integer durationDays) {
        // Verificar si ya existe un tratamiento activo con el mismo nombre para el usuario
        if (treatmentRepository.existsByUserIdAndNameAndStatus(userId, name, TreatmentStatus.ACTIVE)) {
            throw new IllegalArgumentException("Ya existe un tratamiento activo con el nombre: " + name);
        }
        
        Treatment treatment = new Treatment(name, description, userId, durationDays);
        return treatmentRepository.save(treatment);
    }
    
    public List<Treatment> getTreatmentsByUserId(Long userId) {
        return treatmentRepository.findByUserId(userId);
    }
    
    public List<Treatment> getActiveTreatmentsByUserId(Long userId) {
        return treatmentRepository.findByUserIdAndStatus(userId, TreatmentStatus.ACTIVE);
    }
    
    public Optional<Treatment> getTreatmentById(Long treatmentId) {
        return treatmentRepository.findById(treatmentId);
    }
    
    public Treatment updateTreatment(Long treatmentId, String name, String description, Integer durationDays) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        if (name != null && !name.trim().isEmpty()) {
            treatment.setName(name);
        }
        if (description != null) {
            treatment.setDescription(description);
        }
        if (durationDays != null) {
            treatment.setDurationDays(durationDays);
            if (treatment.getStartDate() != null) {
                treatment.setEndDate(treatment.getStartDate().plusDays(durationDays));
            }
        }
        
        return treatmentRepository.save(treatment);
    }
    
    public void completeTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        treatment.completeAt(java.time.LocalDateTime.now());
        treatmentRepository.save(treatment);
    }
    
    public void pauseTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        treatment.pause();
        treatmentRepository.save(treatment);
    }
    
    public void resumeTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        treatment.resume();
        treatmentRepository.save(treatment);
    }
    
    public void cancelTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        treatment.cancel();
        treatmentRepository.save(treatment);
    }
    
    public void deleteTreatment(Long treatmentId) {
        if (!treatmentRepository.existsById(treatmentId)) {
            throw new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId);
        }
        treatmentRepository.deleteById(treatmentId);
    }
}
