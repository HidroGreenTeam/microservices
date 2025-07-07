package com.hidrogreen.treatment_service.treatment.domain.exceptions;

public class TreatmentNotFoundException extends RuntimeException {
    public TreatmentNotFoundException(Long id) {
        super(String.format("Treatment with id %d not found", id));
    }
} 