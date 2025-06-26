package com.hidrogreen.user_service.profiles.domain.model.commands;

public record UpdateFarmerCommand(Long farmerId, String fullName, String phoneNumber, String address) {

}
