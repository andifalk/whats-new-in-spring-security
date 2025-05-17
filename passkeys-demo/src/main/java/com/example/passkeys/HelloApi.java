package com.example.passkeys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HelloApi {

    private static final Logger LOG = LoggerFactory.getLogger(HelloApi.class);

    @GetMapping("/hello")
    public String hello(Object principal) {
        LOG.info("Principal = {}!", principal);

        return switch (principal) {
            case User user -> "Hello, " + user.getUsername() + "!";
            case PublicKeyCredentialUserEntity publicKeyCredentialUserEntity ->
                    "Hello, " + publicKeyCredentialUserEntity.getName();
            case Principal principal1 -> "Hello, " + principal1.getName() + "!";
            case null, default -> "Hello " + principal;
        };
    }
}
