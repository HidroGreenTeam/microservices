package com.hidrogreen.user_service.iam.infrastructure.hashing.bcrypt;


import com.hidrogreen.user_service.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {

}
