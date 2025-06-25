package com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    boolean existsCropsByCropName(String name);
    List<Crop> findCropByFarmerId(Long farmerId);
}