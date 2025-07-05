package com.ayni.crop_service.shared.domain.exceptions;

public class ValidationException extends RuntimeException {
    private final String field;
    private final Object value;

    public ValidationException(String field, Object value, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", field, value, message));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
} 