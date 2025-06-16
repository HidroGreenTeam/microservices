package com.hidrogreen.treatment_activity_service.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.TreatmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    
    List<Treatment> findByUserId(Long userId);
    
    List<Treatment> findByUserIdAndStatus(Long userId, TreatmentStatus status);
    
    @Query("SELECT t FROM Treatment t WHERE t.userId = :userId AND t.status = :status ORDER BY t.createdAt DESC")
    List<Treatment> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("status") TreatmentStatus status);
    
    @Query("SELECT t FROM Treatment t WHERE t.status = :status")
    List<Treatment> findByStatus(@Param("status") TreatmentStatus status);
    
    boolean existsByUserIdAndNameAndStatus(Long userId, String name, TreatmentStatus status);
}
