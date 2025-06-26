package com.hidrogreen.user_service.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.hidrogreen.user_service.iam.domain.model.queries.GetAllUsersQuery;
import com.hidrogreen.user_service.iam.domain.model.queries.GetUserByIdQuery;
import com.hidrogreen.user_service.iam.domain.services.UserQueryService;
import com.hidrogreen.user_service.iam.interfaces.rest.resources.UserResource;
import com.hidrogreen.user_service.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.hidrogreen.user_service.shared.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream()
                .map(UserResourceFromEntityAssembler::toUserResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResources);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(getUserByIdQuery);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "User with id " + userId + " not found"));
        }
        var userResource = UserResourceFromEntityAssembler.toUserResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }
}