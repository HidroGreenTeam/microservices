package com.hidrogreen.user_service.profiles.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.user_service.profiles.domain.model.entities.FarmerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerImageRepository extends JpaRepository<FarmerImage, Long> {
}
