package com.example.tokenexchange.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class ClientApi {
    private static final Logger LOG = LoggerFactory.getLogger(ClientApi.class);
    private static final String TARGET_RESOURCE_SERVER_URL = "http://localhost:9091/api/messages";
    private final RestClient restClient;

    public ClientApi(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/hello")
    public String hello(@RegisteredOAuth2AuthorizedClient("messaging-client-oidc") OAuth2AuthorizedClient oauth2AuthorizedClient) {

        LOG.info("Got authenticated OAuth2 client {}", oauth2AuthorizedClient.getClientRegistration().getClientId());

        RestClient.ResponseSpec responseSpec = restClient.get().uri(TARGET_RESOURCE_SERVER_URL)
                .headers(headers -> headers.setBearerAuth(oauth2AuthorizedClient.getAccessToken().getTokenValue()))
                .retrieve();

        return responseSpec.toEntity(String.class).getBody();
    }

}
