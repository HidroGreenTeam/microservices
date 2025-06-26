package com.hidrogreen.user_service.shared.domain.model.commands;

public record CreateFarmerUserCommand(
    String fullName, 
    String email, 
    String password,
    String phoneNumber,
    String address
) {
} 