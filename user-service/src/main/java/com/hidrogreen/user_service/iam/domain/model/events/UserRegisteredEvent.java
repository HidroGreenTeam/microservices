package com.hidrogreen.user_service.iam.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final String fullName;
    private final String email;
    private final String password;

    public UserRegisteredEvent(Object source, String fullName, String email, String password) {
        super(source);
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

}
