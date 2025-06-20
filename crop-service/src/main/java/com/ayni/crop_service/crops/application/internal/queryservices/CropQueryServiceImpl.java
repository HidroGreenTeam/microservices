package com.ayni.crop_service.crops.application.internal.queryservices;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.queries.GetCropByIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsByProfileIdQuery;
import com.ayni.crop_service.crops.domain.model.queries.GetCropsWithActiveDiseaseQuery;
import com.ayni.crop_service.crops.domain.services.CropQueryService;
import com.ayni.crop_service.crops.domain.model.valueobjects.CropHealthStatus;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Crop query service implementation
 */
@Service
public class CropQueryServiceImpl implements CropQueryService {

    private final CropRepository cropRepository;

    public CropQueryServiceImpl(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @Override
    public Optional<Crop> handle(GetCropByIdQuery query) {
        return cropRepository.findById(query.cropId());
    }

    @Override
    public List<Crop> handle(GetCropsByProfileIdQuery query) {
        return cropRepository.findByProfileId(query.getProfileId());
    }

    @Override
    public List<Crop> handle(GetCropsWithActiveDiseaseQuery query) {
        return cropRepository.findByProfileIdAndDiseasedStatus(query.profileId());
    }
}
