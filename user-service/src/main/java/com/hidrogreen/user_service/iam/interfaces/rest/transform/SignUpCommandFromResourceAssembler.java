package com.hidrogreen.user_service.iam.interfaces.rest.transform;

import com.hidrogreen.user_service.iam.interfaces.rest.resources.SignUpResource;
import com.hidrogreen.user_service.iam.domain.model.commands.SignUpCommand;
import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.stream.Collectors;

public class SignUpCommandFromResourceAssembler {
    
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        List<Role> roles = convertStringRolesToRoles(resource.roles());
        
        return new SignUpCommand(
            resource.fullName(),
            resource.email(), 
            resource.password(),
            roles
        );
    }
    
    private static List<Role> convertStringRolesToRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return List.of(); // Empty list will trigger default role assignment in service
        }
        
        return roleNames.stream()
            .map(roleName -> {
                try {
                    return new Role(Roles.valueOf(roleName.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // If invalid role name, return default user role
                    return new Role(Roles.ROLE_USER);
                }
            })
            .collect(Collectors.toList());
    }
}