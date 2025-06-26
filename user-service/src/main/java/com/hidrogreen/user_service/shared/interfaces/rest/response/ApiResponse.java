package com.hidrogreen.user_service.shared.interfaces.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String details,
        List<ValidationError> errors
) {
    
    // Constructor para respuestas exitosas
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, null);
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }
    
    // Constructores para respuestas de error
    public static <T> ApiResponse<T> error(String message, String details) {
        return new ApiResponse<>(false, message, null, details, null);
    }
    
    public static <T> ApiResponse<T> errorWithMessage(String message) {
        return new ApiResponse<>(false, message, null, null, null);
    }
    
    public static <T> ApiResponse<T> errorWithValidation(String message, List<ValidationError> errors) {
        return new ApiResponse<>(false, message, null, null, errors);
    }
    
    // Record para errores de validación
    public record ValidationError(
            String field,
            Object rejectedValue,
            String message
    ) {}
} 