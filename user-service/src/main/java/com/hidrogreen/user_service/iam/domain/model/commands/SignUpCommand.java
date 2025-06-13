package com.hidrogreen.user_service.iam.domain.model.commands;



import com.hidrogreen.user_service.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String fullName, String email, String password, List<Role> roles) {

}
