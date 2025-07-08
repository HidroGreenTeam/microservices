package com.ayni.crop_service.crops.interfaces.rest;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropImageCommand;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsByFarmerIdQuery;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.transform.CropResourceFromEntityAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.CreateCropCommandFromResourceAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.UpdateCropCommandFromResourceAssembler;
import com.ayni.crop_service.shared.domain.services.ImageUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Crops REST controller
 */
@RestController
@RequestMapping(value = "/api/v1/crops", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Crops", description = "Crop Management API")
@CrossOrigin(origins = "*")
public class CropsController {

    private final CropCommandService cropCommandService;
    private final CropQueryService cropQueryService;
    private final ImageUploadService imageUploadService;

    public CropsController(CropCommandService cropCommandService, CropQueryService cropQueryService, ImageUploadService imageUploadService) {
        this.cropCommandService = cropCommandService;
        this.cropQueryService = cropQueryService;
        this.imageUploadService = imageUploadService;
    }

    /**
     * Get all crops for a farmer
     */
    @GetMapping("/{farmerId}")
    @Operation(summary = "Get all crops for a farmer")
    public ResponseEntity<List<CropResource>> getCropsByFarmerId(@PathVariable Long farmerId) {
        GetCropsByFarmerIdQuery query = new GetCropsByFarmerIdQuery(farmerId);
        List<Crop> crops = cropQueryService.handle(query);
        
        List<CropResource> resources = crops.stream()
            .map(CropResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }

    /**
     * Create a new crop for a farmer
     */
    @PostMapping(value = "/{farmerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new crop for a farmer", 
               description = "Creates a new crop")
    public ResponseEntity<CropResource> createCrop(
            @PathVariable Long farmerId,
            @RequestPart(value = "crop") CreateCropResource cropData,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        try {
            CreateCropCommand command = CreateCropCommandFromResourceAssembler.toCommandFromResource(farmerId, cropData);
            Long cropId = cropCommandService.handle(command);
            
            if (cropId == null || cropId == 0L) {
                return ResponseEntity.badRequest().build();
            }

            // Handle image upload if present
            if (file != null && !file.isEmpty()) {
                try {
                    // Upload image to Cloudinary
                    String imageUrl = imageUploadService.uploadImage(file, "crops");
                    UpdateCropImageCommand imageCommand = new UpdateCropImageCommand(cropId, imageUrl);
                    cropCommandService.handle(imageCommand);
                } catch (Exception e) {
                    // Log error but don't fail the crop creation
                    System.err.println("Failed to upload image: " + e.getMessage());
                }
            }

            // Return the created crop
            GetCropByIdQuery getCropQuery = new GetCropByIdQuery(cropId);
            var crop = cropQueryService.handle(getCropQuery);
            
            if (crop.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(crop.get());
            return new ResponseEntity<>(cropResource, HttpStatus.CREATED);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a specific crop by ID
     */
    @GetMapping("/crop/{cropId}")
    @Operation(summary = "Get a specific crop by ID")
    public ResponseEntity<CropResource> getCropById(@PathVariable Long cropId) {
        GetCropByIdQuery query = new GetCropByIdQuery(cropId);
        var crop = cropQueryService.handle(query);
        
        if (crop.isPresent()) {
            CropResource resource = CropResourceFromEntityAssembler.toResourceFromEntity(crop.get());
            return ResponseEntity.ok(resource);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Update a crop
     */
    @PutMapping("/{cropId}")
    @Operation(summary = "Update a crop")
    public ResponseEntity<CropResource> updateCrop(
            @PathVariable Long cropId, 
            @RequestBody UpdateCropResource resource) {
        
        UpdateCropCommand command = UpdateCropCommandFromResourceAssembler.toCommandFromResource(cropId, resource);
        var crop = cropCommandService.handle(command);
        
        if (crop.isPresent()) {
            CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(crop.get());
            return ResponseEntity.ok(cropResource);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a crop
     */
    @DeleteMapping("/{cropId}")
    @Operation(summary = "Delete a crop")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long cropId) {
        try {
            DeleteCropCommand command = new DeleteCropCommand(cropId);
            cropCommandService.handle(command);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Upload crop image
     */
    @PostMapping(value = "/{cropId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload crop image")
    public ResponseEntity<Map<String, Object>> uploadCropImage(
            @PathVariable Long cropId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Upload image to Cloudinary
            String imageUrl = imageUploadService.uploadImage(file, "crops");
            
            UpdateCropImageCommand command = new UpdateCropImageCommand(cropId, imageUrl);
            var crop = cropCommandService.handle(command);
            
            if (crop.isPresent()) {
                Map<String, Object> response = Map.of(
                    "id", crop.get().getId(),
                    "imageUrl", crop.get().getImageUrl(),
                    "updatedAt", crop.get().getUpdatedAt()
                );
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete crop image
     */
    @DeleteMapping("/{cropId}/cropImage")
    @Operation(summary = "Delete crop image")
    public ResponseEntity<CropResource> deleteCropImage(@PathVariable Long cropId) {
        try {
            // Get the current crop to obtain the image URL before deletion
            GetCropByIdQuery getCropQuery = new GetCropByIdQuery(cropId);
            var existingCrop = cropQueryService.handle(getCropQuery);
            
            if (existingCrop.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            String currentImageUrl = existingCrop.get().getImageUrl();
            
            // Remove the image from the crop record
            var crop = cropCommandService.removeCropImage(cropId);
            
            if (crop.isPresent()) {
                // Delete the image from Cloudinary if it exists
                if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                    imageUploadService.deleteImage(currentImageUrl);
                }
                
                CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(crop.get());
                return ResponseEntity.ok(cropResource);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
