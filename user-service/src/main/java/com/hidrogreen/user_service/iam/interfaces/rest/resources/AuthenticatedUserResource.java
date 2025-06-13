package com.hidrogreen.user_service.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(
    Long id,
    String email,
    String token
) {

}
