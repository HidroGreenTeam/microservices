package com.hidrogreen.user_service.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.hidrogreen.user_service.iam.domain.services.UserCommandService;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.SignInResource;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.SignUpResource;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.UserResource;
import com.hidrogreen.user_service.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.hidrogreen.user_service.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.hidrogreen.user_service.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.hidrogreen.user_service.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.shared.interfaces.rest.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/v1/auth", produces= MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    
    private final UserCommandService userCommandService;
    private final FarmerCommandService farmerCommandService;

    public AuthenticationController(UserCommandService userCommandService, FarmerCommandService farmerCommandService) {
        this.userCommandService = userCommandService;
        this.farmerCommandService = farmerCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in", description = "Authenticate user and get JWT token")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource signInResource) {
        try {
            var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
            var authenticatedUser = userCommandService.handle(signInCommand);

            if(authenticatedUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Authentication failed", "User not found or invalid credentials"));
            }

            var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                authenticatedUser.get().getLeft(), 
                authenticatedUser.get().getRight()
            );
            return ResponseEntity.ok(authenticatedUserResource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication failed", e.getMessage()));
        }
    }

    @PostMapping("/sign-up")
    @Operation(
        summary = "Sign up", 
        description = "Register new user with basic authentication fields"
    )
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        try {
            var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
            var user = userCommandService.handle(signUpCommand);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Registration failed", "Failed to create user"));
            }
            
            // Check if user has ROLE_FARMER and create farmer profile automatically
            var createdUser = user.get();
            boolean hasFarmerRole = createdUser.getRoles().stream()
                    .anyMatch(role -> role.getName().name().equals("ROLE_FARMER"));
                    
            if (hasFarmerRole) {
                try {
                    // Create farmer profile with basic info using fullName directly
                    var createFarmerCommand = new CreateFarmerCommand(
                        createdUser.getId(),
                        createdUser.getFullName(), // Use fullName directly
                        "", // phoneNumber - empty by default
                        ""  // address - empty by default
                    );
                    
                    farmerCommandService.createFarmer(createFarmerCommand);
                } catch (Exception e) {
                    // Log the error but don't fail the user creation
                    System.err.println("Failed to create farmer profile for user " + createdUser.getId() + ": " + e.getMessage());
                }
            }
            
            var userResource = UserResourceFromEntityAssembler.toUserResourceFromEntity(createdUser);
            return new ResponseEntity<>(userResource, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Registration failed", e.getMessage()));
        }
    }
}