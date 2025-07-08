package com.ayni.crop_service.crops.domain.services;

import java.util.List;
import java.util.Optional;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsByFarmerIdQuery;

/**
 * Crop query service interface
 */
public interface CropQueryService {

    /**
     * Get crop by ID
     *
     * @param query the get crop by ID query
     * @return the crop if found
     */
    Optional<Crop> handle(GetCropByIdQuery query);

    /**
     * Get crops by farmer ID
     *
     * @param query the get crops by farmer ID query
     * @return the list of crops
     */
    List<Crop> handle(GetCropsByFarmerIdQuery query);
}
