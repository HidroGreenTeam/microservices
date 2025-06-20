package com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.valueobjects.CropHealthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Crop repository
 */
@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    /**
     * Find crops by profile ID
     *
     * @param profileId the profile ID
     * @return the list of crops
     */
    List<Crop> findByProfileId(Long profileId);

    /**
     * Find crops with diseased health status
     *
     * @param profileId the profile ID
     * @return the list of crops with diseased health status
     */
    @Query("SELECT c FROM Crop c WHERE c.profileId = :profileId AND c.healthStatus = 'DISEASED'")
    List<Crop> findByProfileIdAndDiseasedStatus(@Param("profileId") Long profileId);

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
     * Count crops by profile ID and health status
     *
     * @param profileId the profile ID
     * @param healthStatus the crop health status
     * @return the count
     */
    long countByProfileIdAndHealthStatus(Long profileId, CropHealthStatus healthStatus);

    /**
     * Find crops by profile ID and health status
     *
     * @param profileId the profile ID
     * @param healthStatus the crop health status
     * @return the list of crops
     */
    List<Crop> findByProfileIdAndHealthStatus(Long profileId, CropHealthStatus healthStatus);
}
