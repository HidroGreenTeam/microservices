package com.hidrogreen.user_service.iam.domain.model.aggregates;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class User extends AuditableAbstractAggregateRoot<User> {

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 3, max = 50)
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 3, max = 50)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @Getter
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns= @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String fullName, String email, String password) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public User(String fullName, String email, String password, List<Role> roles) {
        this(fullName, email, password);
        addRoles(roles);
    }

    public final User addRoles(Role roles) {
        this.roles.add(roles);
        return this;
    }
    
    public final User addRoles(List<Role> roles) {
        var validateRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validateRoleSet);
        return this;
    }

}