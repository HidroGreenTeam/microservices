package com.hidrogreen.user_service.iam.infrastructure.persistence.jpa.repositories;


import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.iam.domain.model.valueobjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // busca en la base de datos un rol por su nombre
    Optional<Role> findByName(Roles name);

    // verifica si existe un rol con el nombre
    boolean existsByName(Roles name);

}
