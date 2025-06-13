package com.hidrogreen.user_service.profiles.domain.model.commands;

public record UpdateFarmerCommand(Long farmerId, String username, String phoneNumber) {

}
