package com.hidrogreen.user_service.profiles.interfaces.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetAllFarmersQuery;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByIdQuery;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.profiles.domain.services.FarmerQueryService;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.FarmerResource;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.UpdateFarmerResource;
import com.hidrogreen.user_service.profiles.interfaces.rest.transform.FarmerResourceFromEntityAssembler;
import com.hidrogreen.user_service.profiles.interfaces.rest.transform.UpdateFarmerResourceCommandFromResourceAssembler;
import com.hidrogreen.user_service.shared.interfaces.rest.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/farmers", produces = "application/json")
@Tag(name = "Farmers", description = "Farmers API - Read Only Operations")
@CrossOrigin(origins = "*")
public class FarmerController {

    private final FarmerCommandService farmerCommandService;
    private final FarmerQueryService farmerQueryService;

    public FarmerController(FarmerCommandService farmerCommandService, FarmerQueryService farmerQueryService) {
        this.farmerCommandService = farmerCommandService;
        this.farmerQueryService = farmerQueryService;
    }

    // ============================================
    // READ OPERATIONS
    // ============================================

    @Operation(
            summary = "Get farmer by id",
            description = "Get a farmer by its id"
    )
    @GetMapping("/{farmerId}")
    public ResponseEntity<?> getFarmerById(@PathVariable Long farmerId) {
        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
        }
        var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(farmer.get());
        return ResponseEntity.ok(farmerResource);
    }

    @Operation(
            summary = "Get all farmers",
            description = "Get all farmers"
    )
    @GetMapping
    public ResponseEntity<List<FarmerResource>> getAllFarmers() {
        var getAllFarmerQuery = new GetAllFarmersQuery();
        var farmers = farmerQueryService.getAllFarmers(getAllFarmerQuery);
        var farmerResources = farmers.stream()
                .map(FarmerResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(farmerResources);
    }

    // ============================================
    // UPDATE OPERATIONS
    // ============================================

    @Operation(
            summary = "Update farmer (only the farmer data) | NO IMAGE UPDATE",
            description = "Update a farmer by its id"
    )
    @PutMapping("/{farmerId}")
    public ResponseEntity<?> updateFarmer(@Valid @PathVariable Long farmerId, @RequestBody UpdateFarmerResource updateFarmerResource) {
        try {
            var updateFarmerCommand = UpdateFarmerResourceCommandFromResourceAssembler.toCommandFromResource(updateFarmerResource, farmerId);
            var updatedFarmer = farmerCommandService.updateFarmer(updateFarmerCommand);
            if (updatedFarmer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
            }
            var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(updatedFarmer.get());
            return ResponseEntity.ok(farmerResource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", "Failed to update farmer: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Update farmer image",
            description = "Update the image of a farmer by its id"
    )
    @PutMapping(value = "/{farmerId}/farmerImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFarmerImage(
            @PathVariable Long farmerId,
            @RequestPart("file") MultipartFile file) {
        try {
            Optional<Farmer> farmerOptional = farmerQueryService.getFarmerById(new GetFarmerByIdQuery(farmerId));

            if (farmerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
            }

            Farmer farmer = farmerOptional.get();
            Optional<Farmer> updatedFarmerOptional = farmerCommandService.UpdateFarmerImage(file, farmer);

            if (updatedFarmerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Internal server error", "Error while updating farmer image"));
            }

            var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(updatedFarmerOptional.get());
            return ResponseEntity.ok(farmerResource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error", "Error processing image: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Delete farmer image",
            description = "Delete the image of a farmer by its id"
    )
    @DeleteMapping("/{farmerId}/farmerImage")
    public ResponseEntity<?> deleteFarmerImage(@PathVariable Long farmerId) {
        try {
            Optional<Farmer> updatedFarmerOptional = farmerCommandService.deleteFarmerImage(farmerId);

            if (updatedFarmerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " doesn't have an image"));
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error", "Error while deleting farmer image: " + e.getMessage()));
        }
    }

    // ============================================
    // ENDPOINTS FOR MICROSERVICE COMMUNICATION
    // ============================================

    @Operation(
            summary = "Get farmer phone by farmer id",
            description = "Get farmer phone number by farmer id for inter-service communication"
    )
    @GetMapping("/{farmerId}/phone")
    public ResponseEntity<?> getFarmerPhone(@PathVariable Long farmerId) {
        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
        }
        String phoneNumber = farmer.get().getPhoneNumber();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content when phone is not defined
        }
        return ResponseEntity.ok(phoneNumber);
    }

    @Operation(
            summary = "Get farmer name by farmer id",
            description = "Get farmer full name by farmer id for inter-service communication"
    )
    @GetMapping("/{farmerId}/name")
    public ResponseEntity<?> getFarmerName(@PathVariable Long farmerId) {
        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
        }
        String fullName = farmer.get().getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content when name is not defined
        }
        return ResponseEntity.ok(fullName);
    }

    @Operation(
            summary = "Get farmer by user id",
            description = "Get farmer profile by user id for inter-service communication"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFarmerByUserId(@PathVariable Long userId) {
        var getFarmerByUserIdQuery = new com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByUserIdQuery(userId);
        var farmer = farmerQueryService.getFarmerByUserId(getFarmerByUserIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Farmer with user id " + userId + " not found"));
        }
        var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(farmer.get());
        return ResponseEntity.ok(farmerResource);
    }

    @Operation(
            summary = "Get farmer user id by farmer id",
            description = "Get the user id associated with a farmer for inter-service communication"
    )
    @GetMapping("/{farmerId}/user-id")
    public ResponseEntity<?> getFarmerUserId(@PathVariable Long farmerId) {
        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Farmer with id " + farmerId + " not found"));
        }
        return ResponseEntity.ok(farmer.get().getUserId());
    }

   
}
