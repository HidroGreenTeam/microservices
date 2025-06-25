package com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories;

import com.ayni.crop_service.crops.domain.model.entities.FarmerRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRegistryRepository extends JpaRepository<FarmerRegistry, Long> {
}
