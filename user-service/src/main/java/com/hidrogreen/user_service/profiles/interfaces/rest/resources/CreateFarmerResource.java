package com.hidrogreen.user_service.profiles.interfaces.rest.resources;

public record CreateFarmerResource(
        String username,
        String email,
        String phoneNumber,
        String password
) {
}