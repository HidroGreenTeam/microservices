package com.hidrogreen.user_service.iam.domain.model.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hidrogreen.user_service.iam.domain.model.valueobjects.Roles;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Role {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Roles name;


    public Role(Roles name) {
        this.name = name;
    }

    public String getStringName() {
        return name.name();
    }

    public static Role getDefaultRole() {
        return new Role(Roles.ROLE_USER);
    }

    // este metodo es para convertir un string a un objeto Role
    public static Role toRoleFromName(String name) {
        return new Role(Roles.valueOf(name));
    }

    public static List<Role> validateRoleSet(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of(getDefaultRole());
        }

        return roles;
    }
    public static Role getFirstRole(List<Role> roles) {
        return roles.get(0);
    }



}
