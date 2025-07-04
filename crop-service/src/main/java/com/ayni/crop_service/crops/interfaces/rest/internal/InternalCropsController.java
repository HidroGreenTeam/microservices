package com.ayni.crop_service.crops.interfaces.rest.internal;

import com.ayni.crop_service.crops.application.internal.queryServiceImpl.CropQueryServiceImpl;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.interfaces.rest.transform.CropResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/api/v1/crops")
@Tag(name = "Internal Crops", description = "Internal endpoints for service-to-service communication")
@Hidden
public class InternalCropsController {

    private final CropQueryServiceImpl cropQueryService;

    public InternalCropsController(CropQueryServiceImpl cropQueryService) {
        this.cropQueryService = cropQueryService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get crop by ID (Internal)")
    public ResponseEntity<?> getCropById(@PathVariable Long id) {
        var cropOpt = cropQueryService.handle(new GetCropByIdQuery(id));
        if (cropOpt.isPresent()) {
            return ResponseEntity.ok(CropResourceFromEntityAssembler.toResourceFromEntity(cropOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Resource not found", "Crop with id " + id + " not found"));
        }
    }

    @GetMapping("/{id}/basic")
    @Operation(summary = "Get basic crop info (Internal)")
    public ResponseEntity<BasicCropInfo> getBasicCropInfo(@PathVariable Long id) {
        return cropQueryService.handle(new GetCropByIdQuery(id))
                .map(crop -> {
                    var basicInfo = new BasicCropInfo(
                            crop.getId(),
                            crop.getCropName(),
                            crop.getArea(),
                            crop.getPlantingDate().toString(),
                            crop.getFarmerId(),
                            crop.getCropImage() != null ? crop.getCropImage().getImageUrl() : null,
                            true
                    );
                    return ResponseEntity.ok(basicInfo);
                })
                .orElse(ResponseEntity.ok(
                        new BasicCropInfo(id, null, null, null, null, null, false)));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if crop exists (Internal)")
    public ResponseEntity<Boolean> cropExists(@PathVariable Long id) {
        boolean exists = cropQueryService.handle(new GetCropByIdQuery(id)).isPresent();
        return ResponseEntity.ok(exists);
    }

    public record BasicCropInfo(
            Long id,
            String cropName,
            Long area,
            String plantingDate,
            Long farmerId,
            String imageUrl,
            boolean exists
    ) {}

    public record ErrorResponse(
            String error,
            String message
    ) {}
} 