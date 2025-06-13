package com.hidrogreen.user_service.iam.interfaces.rest.transform;


import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toRoleResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }

}
