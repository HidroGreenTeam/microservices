package com.hidrogreen.user_service.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.hidrogreen.user_service.shared.domain.services.UserProfileService;
import com.hidrogreen.user_service.shared.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/user-profiles", produces = "application/json")
@Tag(name = "User Profiles", description = "Unified User and Profile Management API")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(
            summary = "Delete user and profile",
            description = "Delete a user and all associated profiles"
    )
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUserAndProfile(@PathVariable Long userId) {
        try {
            boolean deleted = userProfileService.deleteUserAndProfile(userId);
            if (deleted) {
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found", "User with id " + userId + " not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error", "Error while deleting user and profile: " + e.getMessage()));
        }
    }

    // ============================================
    // INTERNAL ENDPOINTS FOR SERVICE-TO-SERVICE COMMUNICATION
    // ============================================

    @Operation(
            summary = "Check if farmer exists by ID (Internal)",
            description = "Internal endpoint for service-to-service communication"
    )
    @GetMapping("/internal/farmers/{farmerId}/exists")
    @PreAuthorize("hasRole('SERVICE')")
    @Hidden
    public ResponseEntity<Boolean> existsFarmerById(@PathVariable Long farmerId) {
        var user = userProfileService.getUserByFarmerId(farmerId);
        return ResponseEntity.ok(user.isPresent());
    }

    @Operation(
            summary = "Check if user exists by ID (Internal)",
            description = "Internal endpoint for service-to-service communication"
    )
    @GetMapping("/internal/users/{userId}/exists")
    @PreAuthorize("hasRole('SERVICE')")
    @Hidden
    public ResponseEntity<Boolean> internalUserExists(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.userExists(userId));
    }
} 