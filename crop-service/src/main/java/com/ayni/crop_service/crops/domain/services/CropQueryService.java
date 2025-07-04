package com.ayni.crop_service.crops.domain.services;


import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.queries.GetAllCropsQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsFromAFarmerQuery;

import java.util.List;
import java.util.Optional;

public interface CropQueryService {

    
    List<Crop> handle(GetAllCropsQuery query);
    List<Crop> handle(GetCropsFromAFarmerQuery query);
    Optional<Crop> handle (GetCropByIdQuery query); 


}
