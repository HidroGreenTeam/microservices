package com.hidrogreen.user_service.iam.application.internal.commandServices;


import com.hidrogreen.user_service.iam.domain.model.commands.SeedRolesCommand;
import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.iam.domain.model.valueobjects.Roles;
import com.hidrogreen.user_service.iam.domain.services.RoleCommandService;
import com.hidrogreen.user_service.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if(!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        });
    }

}
