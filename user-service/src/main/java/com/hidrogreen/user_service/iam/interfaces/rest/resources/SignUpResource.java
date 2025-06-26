package com.hidrogreen.user_service.iam.interfaces.rest.resources;

import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpResource(
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50)
    String fullName,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    String password,
    
    // Optional roles - if not provided, ROLE_USER will be assigned by default
    List<String> roles
) {
}
