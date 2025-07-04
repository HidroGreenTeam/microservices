package com.ayni.crop_service.crops.interfaces.rest.controllers;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.queries.GetAllCropsQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsFromAFarmerQuery;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.transform.CreateCropResourceCommandFromResourceAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.CropResourceFromEntityAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.UpdateCropResourceCommandFromResourceAssembler;
 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Crops", description = "Crop Management API - CRUD operations for crop cultivation")
@RequestMapping(value = "api/v1/crops", produces = MediaType.APPLICATION_JSON_VALUE)
public class CropsController {

    private final CropCommandService cropCommandService;
    private final CropQueryService cropQueryService;

    public CropsController(CropCommandService cropCommandService, CropQueryService cropQueryService) {
        this.cropCommandService = cropCommandService;
        this.cropQueryService = cropQueryService;
    }

    @Operation(
            summary = "Get all crops",
            description = "Returns all crops in the system"
    )
    @GetMapping
    public ResponseEntity<List<CropResource>> getAllCrops() {
        var getAllCropsQuery = new GetAllCropsQuery();
        var crops = cropQueryService.handle(getAllCropsQuery);

        var cropResources = crops.stream()
                .map(CropResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(cropResources);
    }

    @Operation(
            summary = "Get crop by ID", 
            description = "Returns detailed information of a specific crop"
    )
    @GetMapping("/{cropId}")
    public ResponseEntity<?> getCropById(@PathVariable Long cropId) {
        var cropOpt = cropQueryService.handle(new GetCropByIdQuery(cropId));
        if (cropOpt.isPresent()) {
            var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(cropOpt.get());
            return ResponseEntity.ok(cropResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get crops from specific farmer",
            description = "Returns all crops associated with a farmer ID"
    )
    @GetMapping("/farmers/{farmerId}/crops")
    public ResponseEntity<?> getCropsFromFarmer(@PathVariable Long farmerId) {
        try {
            GetCropsFromAFarmerQuery query = new GetCropsFromAFarmerQuery(farmerId);
            List<Crop> crops = cropQueryService.handle(query);

            List<CropResource> cropResources = crops.stream()
                    .map(CropResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return ResponseEntity.ok(cropResources);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get farmer crop metrics",
            description = "Returns statistics and metrics for a farmer's crops"
    )
    @GetMapping("/farmers/{farmerId}/metrics")
    public ResponseEntity<?> getFarmerCropMetrics(@PathVariable Long farmerId) {
        try {
            GetCropsFromAFarmerQuery query = new GetCropsFromAFarmerQuery(farmerId);
            List<Crop> crops = cropQueryService.handle(query);

            long totalArea = crops.stream().mapToLong(Crop::getArea).sum();
            long averageArea = crops.isEmpty() ? 0 : totalArea / crops.size();
            
            var metrics = new FarmerCropMetrics(
                    farmerId,
                    crops.size(),
                    totalArea,
                    averageArea,
                    crops.stream().anyMatch(c -> c.getCropImage() != null)
            );

            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Create new crop with image for specific farmer",
            description = "Creates a new crop with validation and optional image upload for a specific farmer"
    )
    @PostMapping(value = "/farmers/{farmerId}/crops", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCrop(
            @PathVariable Long farmerId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("cropName") String cropName,
            @RequestParam("area") Long area,
            @RequestParam("plantingDate") String plantingDate) throws IOException {

        try {
            LocalDate parsedPlantingDate = LocalDate.parse(plantingDate);
            CreateCropResource createCropResource = new CreateCropResource(cropName, area, parsedPlantingDate);
            var createCropCommand = CreateCropResourceCommandFromResourceAssembler.toCommandFromResource(createCropResource, farmerId);

            Long cropId = cropCommandService.handle(createCropCommand, file);

            var cropOpt = cropQueryService.handle(new GetCropByIdQuery(cropId));
            if (cropOpt.isPresent()) {
                var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(cropOpt.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(cropResource);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Update crop data",
            description = "Updates crop information (excluding image)"
    )
    @PutMapping("/{cropId}")
    public ResponseEntity<?> updateCrop(
            @PathVariable Long cropId, 
            @RequestBody @Valid UpdateCropResource updateCropResource) {
        
        var updateCropCommand = UpdateCropResourceCommandFromResourceAssembler.toCommandFromResource(cropId, updateCropResource);

        try {
            var updatedCrop = cropCommandService.handle(updateCropCommand);

            if (updatedCrop.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop.get());
            return ResponseEntity.ok(cropResource);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Update crop image",
            description = "Updates or adds an image to an existing crop"
    )
    @PutMapping(value = "/{cropId}/cropImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCropImage(
            @PathVariable Long cropId,
            @RequestPart("file") MultipartFile file) throws IOException {

        Optional<Crop> cropOptional = cropQueryService.handle(new GetCropByIdQuery(cropId));

        if (cropOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Crop crop = cropOptional.get();
            Optional<Crop> updatedCropOptional = cropCommandService.UpdateCropImage(file, crop);

            if (updatedCropOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Crop updatedCrop = updatedCropOptional.get();
            CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop);

            return ResponseEntity.ok(cropResource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Delete crop image",
            description = "Removes the image from a crop while keeping the crop data"
    )
    @DeleteMapping("/{cropId}/cropImage")
    public ResponseEntity<?> deleteCropImage(@PathVariable Long cropId) throws IOException {
        try {
            Optional<Crop> updatedCropOptional = cropCommandService.deleteCropImage(cropId);

            if (updatedCropOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Crop updatedCrop = updatedCropOptional.get();
            CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop);

            return ResponseEntity.ok(cropResource);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Delete crop (with dependency validation)",
            description = "Deletes a crop after validating no active diagnoses exist"
    )
    @DeleteMapping("/{cropId}")
    public ResponseEntity<?> deleteCrop(@PathVariable Long cropId) {
        try {
            var deleteCropCommand = new DeleteCropCommand(cropId);
            cropCommandService.handle(deleteCropCommand);

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Search crops by name",
            description = "Search crops by name pattern"
    )
    @GetMapping("/search")
    public ResponseEntity<List<CropResource>> searchCropsByName(@RequestParam String name) {
        var allCrops = cropQueryService.handle(new GetAllCropsQuery());
        
        var filteredCrops = allCrops.stream()
                .filter(crop -> crop.getCropName().toLowerCase().contains(name.toLowerCase()))
                .toList();

        var cropResources = filteredCrops.stream()
                .map(CropResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(cropResources);
    }

    @Operation(
            summary = "Test authentication with JSON POST",
            description = "Test endpoint to verify authentication works with JSON POST"
    )
    @PostMapping("/test-auth")
    public ResponseEntity<?> testAuth(@RequestBody @Valid CreateCropResource createCropResource) {
        return ResponseEntity.ok().body(java.util.Map.of(
            "message", "Authentication successful",
            "received", createCropResource
        ));
    }

    @Operation(
            summary = "Test authentication with multipart POST",
            description = "Test endpoint to verify authentication works with multipart POST"
    )
    @PostMapping(value = "/test-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> testMultipartAuth(
            @RequestPart("crop") @Valid CreateCropResource createCropResource) {
        return ResponseEntity.ok().body(java.util.Map.of(
            "message", "Multipart authentication successful",
            "received", createCropResource
        ));
    }

    public record FarmerCropMetrics(
            Long farmerId,
            Integer totalCrops,
            Long totalArea,
            Long averageArea,
            Boolean hasImages
    ) {}
}