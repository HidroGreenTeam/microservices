package com.hidrogreen.user_service.profiles.interfaces.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetAllFarmersQuery;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByIdQuery;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.profiles.domain.services.FarmerQueryService;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.CreateFarmerResource;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.FarmerResource;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.UpdateFarmerResource;
import com.hidrogreen.user_service.profiles.interfaces.rest.transform.CreateFarmerResourceCommandFromResourceAssembler;
import com.hidrogreen.user_service.profiles.interfaces.rest.transform.FarmerResourceFromEntityAssembler;
import com.hidrogreen.user_service.profiles.interfaces.rest.transform.UpdateFarmerResourceCommandFromResourceAssembler;
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
@Tag(name = "Farmers", description = "Farmers API")
@CrossOrigin(origins = "*")
public class FarmerController {

    private final FarmerCommandService farmerCommandService;
    private final FarmerQueryService farmerQueryService;


    public FarmerController(FarmerCommandService farmerCommandService, FarmerQueryService farmerQueryService) {
        this.farmerCommandService = farmerCommandService;
        this.farmerQueryService = farmerQueryService;
    }

    @Operation(
            summary = "Create farmer",
            description = "Create a new farmer"
    )
    @PostMapping
    public ResponseEntity<FarmerResource> createFarmer(@Valid @RequestBody CreateFarmerResource createFarmerResource) {

        var createFarmerCommand = CreateFarmerResourceCommandFromResourceAssembler.toCommandFromResource(createFarmerResource);
        var farmerId = farmerCommandService.createFarmer(createFarmerCommand);
        if (farmerId == null) {
            return ResponseEntity.badRequest().build();
        }

        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(farmer.get());
        return new ResponseEntity<>(farmerResource, HttpStatus.CREATED);

    }

    @Operation(
            summary = "Get farmer by id",
            description = "Get a farmer by its id"
    )
    @GetMapping("/{farmerId}")
    public ResponseEntity<FarmerResource> getFarmerById(@PathVariable Long farmerId) {
        var getFarmerByIdQuery = new GetFarmerByIdQuery(farmerId);
        var farmer = farmerQueryService.getFarmerById(getFarmerByIdQuery);
        if (farmer.isEmpty()) {
            return ResponseEntity.badRequest().build();
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
        return ResponseEntity.ok(farmers.stream().map(FarmerResourceFromEntityAssembler::toResourceFromEntity).collect(Collectors.toList()));
    }


    @Operation(
            summary = "Update farmer (only the farmer data) | NO IMAGE UPDATE",
            description = "Update a farmer by its id"
    )
    @PutMapping("/{farmerId}")
    public ResponseEntity<FarmerResource> updateFarmer(@Valid @PathVariable Long farmerId, @RequestBody UpdateFarmerResource updateFarmerResource) {
        var updateFarmerCommand = UpdateFarmerResourceCommandFromResourceAssembler.toCommandFromResource(updateFarmerResource, farmerId);
        var updatedFarmer = farmerCommandService.updateFarmer(updateFarmerCommand);
        if (updatedFarmer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var farmerResource = FarmerResourceFromEntityAssembler.toResourceFromEntity(updatedFarmer.get());
        return ResponseEntity.ok(farmerResource);
    }

    @Operation(
            summary = "Update farmer image",
            description = "Update the image of a farmer by its id"
    )
    @PutMapping(value = "/{farmerId}/farmerImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmerResource> updateFarmerImage(
            @PathVariable Long farmerId,
            @RequestPart("file")MultipartFile file) throws IOException {

        // buscamos el id
        Optional<Farmer> farmerOptional = farmerQueryService.getFarmerById(new GetFarmerByIdQuery(farmerId));


        // si no existe el farmer
        if (farmerOptional.isEmpty()) {
            return ResponseEntity.notFound().header("message", "Farmer with id " + farmerId + " not found").build();
        }

        Farmer farmer = farmerOptional.get();

        // actualizamos la imagen
        Optional<Farmer> updatedFarmerOptional = farmerCommandService.UpdateFarmerImage(file, farmer);

        // si no se pudo actualizar la imagen
        if (updatedFarmerOptional.isEmpty()) {
            return ResponseEntity.badRequest().header("message", "Error while updating farmer image").build();
        }

        // retornamos el farmer actualizado
        return ResponseEntity.ok(FarmerResourceFromEntityAssembler.toResourceFromEntity(updatedFarmerOptional.get()));

    }

    @Operation(
            summary = "Delete farmer",
            description = "Delete a farmer by its id"
    )
    @DeleteMapping("/{farmerId}")
    public ResponseEntity<?> deleteFarmer(@PathVariable Long farmerId) {
        farmerCommandService.deleteFarmer(farmerId);
        return ResponseEntity.ok("Farmer with given id successfully deleted");
    }

    @Operation(
            summary = "Delete farmer image",
            description = "Delete the image of a farmer by its id"
    )
    @DeleteMapping("/{farmerId}/farmerImage")
    public ResponseEntity<FarmerResource> deleteFarmerImage(@PathVariable Long farmerId) throws IOException {

        // delegar la eliminación de la imagen al servicio
        Optional<Farmer> updatedFarmerOptional = farmerCommandService.deleteFarmerImage(farmerId);

        // si no se pudo eliminar la imagen
        if (updatedFarmerOptional.isEmpty()) {
            return ResponseEntity.badRequest().header("message", "Error while deleting farmer image").build();
        }

        // retornamos el farmer actualizado
        Farmer updatedFarmer = updatedFarmerOptional.get();

        return ResponseEntity.ok(FarmerResourceFromEntityAssembler.toResourceFromEntity(updatedFarmer));
    }
}
