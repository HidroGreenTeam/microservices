package com.hidrogreen.user_service.iam.interfaces.rest.transform;


import com.hidrogreen.user_service.iam.domain.model.commands.SignInCommand;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.email(), signInResource.password());
    }

}
