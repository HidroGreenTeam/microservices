package com.hidrogreen.user_service.iam.domain.services;


import com.hidrogreen.user_service.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
