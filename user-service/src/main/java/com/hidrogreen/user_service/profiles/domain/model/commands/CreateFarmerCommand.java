package com.hidrogreen.user_service.profiles.domain.model.commands;

public record CreateFarmerCommand(Long userId, String fullName, String phoneNumber, String address) {
}
