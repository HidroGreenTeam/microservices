package com.hidrogreen.treatment_service.shared.domain.exceptions;

public class ValidationException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public ValidationException(String field, Object value, String message) {
        super(String.format("Validation error for field '%s' with value '%s': %s", field, value, message));
        this.field = field;
        this.value = value;
    }
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
} 