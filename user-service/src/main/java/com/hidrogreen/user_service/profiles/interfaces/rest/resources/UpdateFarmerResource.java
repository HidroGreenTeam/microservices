package com.hidrogreen.user_service.profiles.interfaces.rest.resources;

public record UpdateFarmerResource(
        String fullName,
        String phoneNumber,
        String address
) {
}
