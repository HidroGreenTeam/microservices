package com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;

/**
 * Crop repository
 */
@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    /**
     * Find crops by farmer ID
     *
     * @param farmerId the farmer ID
     * @return the list of crops
     */
    List<Crop> findByFarmerId(Long farmerId);

    /**
     * Find crops by crop name
     *
     * @param cropName the crop name
     * @return the list of crops
     */
    @Query("SELECT c FROM Crop c WHERE c.cropName = :cropName")
    List<Crop> findByCropName(@Param("cropName") String cropName);

    /**
     * Find crops by location
     *
     * @param location the location
     * @return the list of crops
     */
    List<Crop> findByLocationContainingIgnoreCase(String location);

    /**
     * Count crops by farmer ID
     *
     * @param farmerId the farmer ID
     * @return the count
     */
    long countByFarmerId(Long farmerId);
}
