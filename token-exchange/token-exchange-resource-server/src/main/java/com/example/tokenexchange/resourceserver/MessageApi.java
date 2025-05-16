package com.example.tokenexchange.resourceserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/messages")
public class MessageApi {
    private static final Logger LOG = LoggerFactory.getLogger(MessageApi.class);
    private static final String TARGET_RESOURCE_SERVER_URL = "http://localhost:9092/api/messages";
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final RestClient restClient;

    public MessageApi(OAuth2AuthorizedClientManager authorizedClientManager, RestClient restClient) {
        this.authorizedClientManager = authorizedClientManager;
        this.restClient = restClient;
    }

    @GetMapping
    public String message(JwtAuthenticationToken jwtAuthentication) {
        LOG.info("Called the token exchange resource server with exchanged token subject {} and audience {}",
                jwtAuthentication.getToken().getSubject(),
                jwtAuthentication.getToken().getAudience());

        LOG.info("Performing token exchange to call the target resource server...");

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("messaging-client-token-exchange")
                .principal(jwtAuthentication)
                .build();
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager.authorize(authorizeRequest);
        LOG.info("Performed token exchange");

        assert authorizedClient != null;
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        if (accessToken == null) {
            LOG.warn("No access token");
            return "I am a message from the token exchange resource server";
        }
        LOG.info("Call target resource server with exchanged token...");
        RestClient.ResponseSpec responseSpec = restClient.get().uri(TARGET_RESOURCE_SERVER_URL)
                .headers(headers -> headers.setBearerAuth(accessToken.getTokenValue()))
                .retrieve();
        ResponseEntity<String> responseEntity = responseSpec.toEntity(String.class);
        LOG.info("Successfully called the target resource server with exchanged token");
        return "I am a message from the token exchange resource server with " + responseEntity.getBody();
    }
}
