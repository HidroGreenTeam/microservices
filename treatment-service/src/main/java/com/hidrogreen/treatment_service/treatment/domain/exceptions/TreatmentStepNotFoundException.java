package com.hidrogreen.treatment_service.treatment.domain.exceptions;

public class TreatmentStepNotFoundException extends RuntimeException {
    public TreatmentStepNotFoundException(Long id) {
        super(String.format("Treatment step with id %d not found", id));
    }
} 