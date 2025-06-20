package com.ayni.crop_service.crops.interfaces.rest;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropStatusCommand;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsByProfileIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsWithActiveDiseaseQuery;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropStatusResource;
import com.ayni.crop_service.crops.interfaces.rest.transform.CropResourceFromEntityAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.CreateCropCommandFromResourceAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.UpdateCropStatusCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Crops REST controller
 */
@RestController
@RequestMapping(value = "/api/v1/crops", produces = MediaType.APPLICATION_JSON_VALUE)
public class CropsController {

    private final CropCommandService cropCommandService;
    private final CropQueryService cropQueryService;

    public CropsController(CropCommandService cropCommandService, CropQueryService cropQueryService) {
        this.cropCommandService = cropCommandService;
        this.cropQueryService = cropQueryService;
    }

    /**
     * Create a new crop
     */
    @PostMapping
    public ResponseEntity<CropResource> createCrop(@RequestBody CreateCropResource resource) {
        var createCropCommand = CreateCropCommandFromResourceAssembler.toCommandFromResource(resource);
        var cropId = cropCommandService.handle(createCropCommand);
        if (cropId == null || cropId == 0L) return ResponseEntity.badRequest().build();
        var getCropByIdQuery = new GetCropByIdQuery(cropId);
        var crop = cropQueryService.handle(getCropByIdQuery);
        if (crop.isEmpty()) return ResponseEntity.notFound().build();
        var cropEntity = crop.get();
        var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(cropEntity);
        return new ResponseEntity<>(cropResource, HttpStatus.CREATED);
    }

    /**
     * Get crop by ID
     */
    @GetMapping("/{cropId}")
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
     * Get crops by profile ID
     */
    @GetMapping
    public ResponseEntity<List<CropResource>> getCropsByProfileId(@RequestParam Long profileId) {
        GetCropsByProfileIdQuery query = new GetCropsByProfileIdQuery(profileId);
        List<Crop> crops = cropQueryService.handle(query);
        
        List<CropResource> resources = crops.stream()
            .map(CropResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }

    /**
     * Get crops with active diseases
     */
    @GetMapping("/diseased")
    public ResponseEntity<List<CropResource>> getCropsWithActiveDisease(@RequestParam Long profileId) {
        GetCropsWithActiveDiseaseQuery query = new GetCropsWithActiveDiseaseQuery(profileId);
        List<Crop> crops = cropQueryService.handle(query);
        
        List<CropResource> resources = crops.stream()
            .map(CropResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }

    /**
     * Update crop status
     */
    @PatchMapping("/{cropId}/status")
    public ResponseEntity<CropResource> updateCropStatus(@PathVariable Long cropId, 
                                                       @RequestBody UpdateCropStatusResource resource) {
        UpdateCropStatusCommand command = UpdateCropStatusCommandFromResourceAssembler
            .toCommandFromResource(cropId, resource);
        var crop = cropCommandService.handle(command);
        
        if (crop.isPresent()) {
            CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(crop.get());
            return ResponseEntity.ok(cropResource);
        }
        
        return ResponseEntity.notFound().build();
    }
}
