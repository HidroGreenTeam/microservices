package com.ayni.crop_service.shared.interfaces.rest.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String details;
    private List<ValidationError> errors;

    private ApiResponse(boolean success, String message, T data, String details) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.details = details;
    }

    private ApiResponse(boolean success, String message, T data, List<ValidationError> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    // Success response factory methods
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, (String) null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data, (String) null);
    }

    // Error response factory methods
    public static <T> ApiResponse<T> error(String message, String details) {
        return new ApiResponse<>(false, message, null, details);
    }

    public static <T> ApiResponse<T> errorWithMessage(String message) {
        return new ApiResponse<>(false, message, null, (String) null);
    }

    public static <T> ApiResponse<T> errorWithValidation(String message, List<ValidationError> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    // Validation error class
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;

        public ValidationError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        // Getters and setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 