package com.hidrogreen.user_service.iam.interfaces.rest.transform;


import com.hidrogreen.user_service.iam.domain.model.aggregates.User;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
            user.getId(),
            user.getEmail(),
            token
        );
    }

}
