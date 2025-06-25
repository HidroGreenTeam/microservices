package com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices;

import java.util.Optional;

public interface ExternalUserService {
    Optional<UserInfo> getUserById(Long userId);
    
    public static record UserInfo(
        Long id,
        String email,
        String firstName,
        String lastName
    ) {}
}
