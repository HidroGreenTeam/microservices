package com.hidrogreen.user_service.profiles.interfaces.rest.resources;

public record FarmerResource(
        Long id,
        Long userId,
        String fullName,
        String phoneNumber,
        String address,
        String imageUrl
) {
}
