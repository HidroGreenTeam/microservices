package com.hidrogreen.user_service.profiles.domain.services;

import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetAllFarmersQuery;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByIdQuery;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface FarmerQueryService {
    List<Farmer> getAllFarmers(GetAllFarmersQuery query);
    Optional<Farmer> getFarmerById(GetFarmerByIdQuery query);
    Optional<Farmer> getFarmerByUserId(GetFarmerByUserIdQuery query);
}
