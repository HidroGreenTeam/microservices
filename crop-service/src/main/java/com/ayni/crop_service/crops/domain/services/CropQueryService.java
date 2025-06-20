package com.ayni.crop_service.crops.domain.services;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsByProfileIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsWithActiveDiseaseQuery;

import java.util.List;
import java.util.Optional;

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
     * Get crops by profile ID
     *
     * @param query the get crops by profile ID query
     * @return the list of crops
     */
    List<Crop> handle(GetCropsByProfileIdQuery query);

    /**
     * Get crops with active diseases
     *
     * @param query the get crops with active disease query
     * @return the list of crops with active diseases
     */
    List<Crop> handle(GetCropsWithActiveDiseaseQuery query);
}
