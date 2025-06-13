package com.hidrogreen.user_service.profiles.domain.model.commands;

public record CreateFarmerCommand(String username, String email, String phoneNumber, String password) {
}
