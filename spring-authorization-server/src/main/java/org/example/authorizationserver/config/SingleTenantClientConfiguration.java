package org.example.authorizationserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("!multi-tenancy")
@Configuration
public class SingleTenantClientConfiguration {

    public static final String TOKEN_EXCHANGE_CLIENT = "token-exchange-client";
    public static final String MESSAGING_CLIENT = "messaging-client";
    public static final String CLIENT_CREDENTIALS_CLIENT = "client-credentials-client";

    private static final Logger LOG = LoggerFactory.getLogger(SingleTenantClientConfiguration.class);

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient publicOidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(MESSAGING_CLIENT)
                .clientSecret("{noop}public_secret")
                .clientAuthenticationMethods(m -> {
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    m.add(ClientAuthenticationMethod.NONE);
                })
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUris(u -> {
                    u.add("http://127.0.0.1:8080/client/login/oauth2/code/messaging-client-oidc");
                    u.add("http://localhost:8080/client/login/oauth2/code/messaging-client-oidc");
                })
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.PHONE)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofMinutes(30)).build())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).requireProofKey(true).build())
                .build();

        RegisteredClient clientCredentialsClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(CLIENT_CREDENTIALS_CLIENT)
                .clientSecret("{noop}client_credentials_secret")
                .clientAuthenticationMethods(m -> {
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                })
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("openid")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofMinutes(30)).build())
                .build();

        RegisteredClient tokenExchangeClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(TOKEN_EXCHANGE_CLIENT)
                .clientSecret("{noop}exchange_secret")
                .clientAuthenticationMethods(m -> {
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    m.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                })
                .authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
                .scope("message.read")
                .scope("message.write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofHours(8)).build())
                .build();

        LOG.info("Registering public OIDC Client: {}", publicOidcClient);
        LOG.info("Registering Client Credentials Client: {}", clientCredentialsClient);
        LOG.info("Registering Token Exchange Client: {}", tokenExchangeClient);

        return new InMemoryRegisteredClientRepository(publicOidcClient, clientCredentialsClient, tokenExchangeClient);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                if (context.getRegisteredClient().getClientId().equals(TOKEN_EXCHANGE_CLIENT)) {
                    context.getClaims().audience(List.of("http://localhost:9092/api/messages"));
                }
                context.getClaims().claims((claims) -> {
                    Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                            .stream()
                            .map(c -> c.replaceFirst("^ROLE_", ""))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    claims.put("roles", roles);
                });
            }
        };
    }
}
