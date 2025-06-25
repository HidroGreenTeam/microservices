package com.ayni.crop_service.crops.interfaces.rest.controllers;


import com.ayni.crop_service.crops.application.internal.commandServiceImpl.CropCommandServiceImpl;
import com.ayni.crop_service.crops.application.internal.commandServiceImpl.CropImageServiceImpl;
import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateIrrigationTypeCommand;
import com.ayni.crop_service.crops.domain.model.queries.GetAllCropsQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsFromAFarmerQuery;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateIrrigationTypeResource;
import com.ayni.crop_service.crops.interfaces.rest.transform.CreateCropResourceCommandFromResourceAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.CropResourceFromEntityAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.UpdateCropResourceCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Crops", description = "Crops management")
@RequestMapping(value = "api/v1/crops", produces = MediaType.APPLICATION_JSON_VALUE)
public class CropsController {

    private final CropCommandService cropCommandService;
    private final CropQueryService cropQueryService;

    @Autowired
    public CropsController(CropCommandService cropCommandService, CropQueryService cropQueryService, CropCommandServiceImpl cropCommandServiceImpl, CropImageServiceImpl cropImageServiceImpl) {
        this.cropCommandService = cropCommandService;
        this.cropQueryService = cropQueryService;
    }

    @Operation(
            summary = "Get all crops",
            description = "Returns a list of all crops"
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
            summary = "Create a crop with an image",
            description = "Creates a new crop using the data provided in the request body and the image provided in the request form-data"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CropResource> createCrop(
            @RequestPart("file") MultipartFile file,
            @RequestPart("crop") @Valid CreateCropResource createCropResource) throws IOException {

        // Crear el comando de cultivo
        var createCropCommand = CreateCropResourceCommandFromResourceAssembler.toCommandFromResource(createCropResource);

        // Llamar al servicio que maneja tanto la creación del cultivo como la subida de la imagen
        Long cropId = cropCommandService.handle(createCropCommand, file);

        // Devolver el recurso del cultivo creado
        return cropQueryService.handle(new GetCropByIdQuery(cropId))
                .map(cropEntity -> new ResponseEntity<>(
                        CropResourceFromEntityAssembler.toResourceFromEntity(cropEntity), HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }


    @Operation(
            summary = "Get a crop by cropId",
            description = "Returns the crop with the given cropId"
    )
    @GetMapping("/{cropId}")
    public ResponseEntity<CropResource> getCropById(@PathVariable Long cropId) {
        return cropQueryService.handle(new GetCropByIdQuery(cropId))
                .map(cropEntity -> new ResponseEntity<>(
                        CropResourceFromEntityAssembler.toResourceFromEntity(cropEntity), HttpStatus.OK))
                .orElse(ResponseEntity.notFound().header(
                        "message", "Crop with id " + cropId + " not found"
                ).build());
    }





    @Operation(
            summary = "Get all crops for a specific farmer",
            description = "Returns a list of all crops associated with the given farmerId"
    )
    @GetMapping("/farmer/{farmerId}/crops")
    public ResponseEntity<List<CropResource>> getCropsFromFarmer(@PathVariable Long farmerId) {
        // Crear la consulta usando el farmerId
        GetCropsFromAFarmerQuery query = new GetCropsFromAFarmerQuery(farmerId);

        // Llamar al servicio que maneja la lógica de consulta
        List<Crop> crops = cropQueryService.handle(query);

        // Convertir los cultivos obtenidos a recursos para enviarlos como respuesta
        List<CropResource> cropResources = crops.stream()
                .map(CropResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        // Devolver los cultivos como una respuesta HTTP
        return ResponseEntity.ok(cropResources);
    }




    @Operation(
            summary = "Update a crop by cropId (only the crop data) | NO IMAGE UPDATE",
            description = "Updates the crop with the given cropId using the data provided in the request body"
    )
    @PutMapping("/{cropId}")
    public ResponseEntity<CropResource> updateCrop(@PathVariable Long cropId, @RequestBody UpdateCropResource updateCropResource) {
        var updateCropCommand = UpdateCropResourceCommandFromResourceAssembler.toCommandFromResource(cropId, updateCropResource);

        var updatedCrop = cropCommandService.handle(updateCropCommand); // actualizamos sin la imagen

        if (updatedCrop.isEmpty()) {
            return ResponseEntity.notFound().header(
                    "message", "Crop with id " + cropId + " not found"
            ).build();
        }

        var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop.get());
        return ResponseEntity.ok(cropResource);
    }



    @Operation(
            summary = "Update a crop image by cropId",
            description = "Updates the crop image with the given cropId using the image provided in the request body"
    )
    @PutMapping(value = "/{cropId}/cropImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CropResource> updateCropImage(
            @PathVariable Long cropId,
            @RequestPart("file") MultipartFile file) throws IOException {

        // Encontramos el cultivo por ID
        Optional<Crop> cropOptional = cropQueryService.handle(new GetCropByIdQuery(cropId));

        if (cropOptional.isEmpty()) {
            return ResponseEntity.notFound().header("message", "Crop with id " + cropId + " not found").build();
        }

        Crop crop = cropOptional.get();

        // Subimos la nueva imagen a Cloudinary y eliminamos la anterior si existe
        Optional<Crop> updatedCropOptional = cropCommandService.UpdateCropImage(file, crop);

        if (updatedCropOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Crop updatedCrop = updatedCropOptional.get();

        // Convertimos el cultivo a un recurso para devolverlo al cliente
        CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop);

        return ResponseEntity.ok(cropResource);
    }





    @Operation(
            summary = "Update the irrigation type of a crop by cropId",
            description = "Updates the irrigation type of the crop with the given cropId using the irrigation type [Manual, Automatic]"
    )
    @PatchMapping("/{cropId}/irrigationType")
    public ResponseEntity<CropResource> updateIrrigationType(@PathVariable Long cropId, @RequestBody UpdateIrrigationTypeResource updateIrrigationTypeResource) {
        var updateIrrigationTypeCommand = new UpdateIrrigationTypeCommand(
                cropId,
                updateIrrigationTypeResource.irrigationType()
        );

        Optional<Crop> updatedIrrigation = cropCommandService.handle(updateIrrigationTypeCommand);

        if (updatedIrrigation.isEmpty()) {
            return ResponseEntity.notFound().header(
                    "message", "Crop with id " + cropId + " not found"
            ).build();
        }

        var cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedIrrigation.get());
        return ResponseEntity.ok(cropResource);
    }




    @Operation(
            summary = "Delete a crop by cropId",
            description = "Deletes the crop with the given cropId"
    )
    @DeleteMapping("/{cropId}")
    public ResponseEntity<?> deleteCrop(@PathVariable Long cropId) {
        var deleteCropCommand = new DeleteCropCommand(cropId);
        cropCommandService.handle(deleteCropCommand);

        return ResponseEntity.ok("Crop with id " + cropId + " deleted");
    }



    @Operation(
            summary = "Delete a crop image by cropId",
            description = "Deletes the image of the crop with the given cropId"
    )
    @DeleteMapping("/{cropId}/cropImage")
    public ResponseEntity<CropResource> deleteCropImage(@PathVariable Long cropId) throws IOException {
        // Delegar al servicio para que maneje la eliminación de la imagen
        Optional<Crop> updatedCropOptional = cropCommandService.deleteCropImage(cropId);

        if (updatedCropOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Crop updatedCrop = updatedCropOptional.get();

        // Convertir el cultivo actualizado a un recurso para devolverlo al cliente
        CropResource cropResource = CropResourceFromEntityAssembler.toResourceFromEntity(updatedCrop);

        return ResponseEntity.ok(cropResource);
    }



}