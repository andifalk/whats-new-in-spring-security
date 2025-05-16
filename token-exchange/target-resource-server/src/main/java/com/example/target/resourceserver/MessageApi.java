package com.example.target.resourceserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageApi {

    private static final Logger LOG = LoggerFactory.getLogger(MessageApi.class);

    @GetMapping
    public String message(JwtAuthenticationToken jwtAuthentication) {
        LOG.info("Called the target resource server with exchanged token  subject {} and audience {}",
                jwtAuthentication.getToken().getSubject(),
                jwtAuthentication.getToken().getAudience());
        return "I am a message from the target resource server";
    }
}
