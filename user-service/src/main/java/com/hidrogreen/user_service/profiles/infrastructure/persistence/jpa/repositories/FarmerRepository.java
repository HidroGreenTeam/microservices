package com.hidrogreen.user_service.profiles.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    boolean existsByEmail(String email);
    Optional<Farmer> findByEmail(String email);
}
