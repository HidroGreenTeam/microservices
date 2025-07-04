package com.ayni.crop_service.crops.application.internal.queryServiceImpl;


import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.queries.GetAllCropsQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsFromAFarmerQuery;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;
import com.ayni.crop_service.crops.application.internal.services.ExternalValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CropQueryServiceImpl implements CropQueryService {

    private final CropRepository cropRepository;
    private final ExternalValidationService externalValidationService;

    @Autowired
    public CropQueryServiceImpl(CropRepository cropRepository, ExternalValidationService externalValidationService) {
        this.cropRepository = cropRepository;
        this.externalValidationService = externalValidationService;
    }

    @Override
    public List<Crop> handle(GetAllCropsQuery query) {
        return cropRepository.findAll();
    }

    @Override
    public List<Crop> handle(GetCropsFromAFarmerQuery query) {
        
        externalValidationService.validateFarmerExists(query.farmerId());
        
        return cropRepository.findCropByFarmerId(query.farmerId());
    }

    @Override
    public Optional<Crop> handle(GetCropByIdQuery query) {
        return cropRepository.findById(query.id());
    }


}
