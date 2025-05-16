package org.example.authorizationserver.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;

import java.util.List;

@Profile("multi-tenancy")
@Configuration
public class MultiTenancyConfiguration {

    public static final String ISSUER_1 = "issuer1";
    public static final String ISSUER_2 = "issuer2";
    public static final List<String> REDIRECT_URIS = List.of(
            "http://localhost:8080/login/oauth2/code/spring",
            "http://127.0.0.1:8080/login/oauth2/code/spring",
            "http://127.0.0.1:8080/callback",
            "http://localhost:8080/callback");

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .multipleIssuersAllowed(true)
                .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(TenantPerIssuerComponentRegistry componentRegistry) {

        RegisteredClientRepository issuer1RegisteredClientRepository =
                new InMemoryRegisteredClientRepository(
                        RegisteredClient.withId("exchange")
                                .clientId("exchange")
                                .clientSecret("{noop}password")
                                .clientAuthenticationMethods(m -> m.addAll(List.of(
                                        ClientAuthenticationMethod.NONE,
                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
                                )
                                .authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
                                .redirectUris(u -> u.addAll(REDIRECT_URIS))
                                .scope("exchange")
                                .clientSettings(
                                        ClientSettings
                                                .builder()
                                                .requireAuthorizationConsent(true)
                                                .build()
                                )
                                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build())
                                .build(),
                        RegisteredClient.withId("client1")
                                .clientId("client1")
                                .clientSecret("{noop}secret")
                                .clientAuthenticationMethods(m -> m.addAll(List.of(
                                        ClientAuthenticationMethod.NONE,
                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
                                )
                                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                .redirectUris(u -> u.addAll(REDIRECT_URIS))
                                .scopes(s -> s.addAll(List.of("openid", "scope1")))
                                .clientSettings(
                                        ClientSettings
                                                .builder()
                                                .requireProofKey(true)
                                                .requireAuthorizationConsent(true)
                                                .build()
                                )
                                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build())
                                .build()
                );

        RegisteredClientRepository issuer2RegisteredClientRepository =
                new InMemoryRegisteredClientRepository(
                        RegisteredClient.withId("client2")
                                .clientId("client2")
                                .clientSecret("{noop}secret")
                                .clientAuthenticationMethods(m -> m.addAll(List.of(
                                        ClientAuthenticationMethod.NONE,
                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
                                )
                                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                .redirectUris(u -> u.addAll(REDIRECT_URIS))
                                .scopes(s -> s.addAll(List.of("openid", "scope2")))
                                .clientSettings(
                                        ClientSettings
                                                .builder()
                                                .requireProofKey(true)
                                                .requireAuthorizationConsent(true)
                                                .build()
                                )
                                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build())
                                .build()
                );

        componentRegistry.register(ISSUER_1, RegisteredClientRepository.class, issuer1RegisteredClientRepository);
        componentRegistry.register(ISSUER_2, RegisteredClientRepository.class, issuer2RegisteredClientRepository);

        return new DelegatingRegisteredClientRepository(componentRegistry);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
            TenantPerIssuerComponentRegistry componentRegistry) {

        componentRegistry.register(ISSUER_1, OAuth2AuthorizationService.class,
                new InMemoryOAuth2AuthorizationService());
        componentRegistry.register(ISSUER_2, OAuth2AuthorizationService.class,
                new InMemoryOAuth2AuthorizationService());

        return new DelegatingOAuth2AuthorizationService(componentRegistry);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            TenantPerIssuerComponentRegistry componentRegistry) {

        componentRegistry.register(ISSUER_1, OAuth2AuthorizationConsentService.class,
                new InMemoryOAuth2AuthorizationConsentService());
        componentRegistry.register(ISSUER_2, OAuth2AuthorizationConsentService.class,
                new InMemoryOAuth2AuthorizationConsentService());

        return new DelegatingOAuth2AuthorizationConsentService(componentRegistry);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(TenantPerIssuerComponentRegistry componentRegistry) {
        componentRegistry.register(ISSUER_1, JWKSet.class, new JWKSet(Jwks.generateRsa()));
        componentRegistry.register(ISSUER_2, JWKSet.class, new JWKSet(Jwks.generateRsa()));

        return new DelegatingJWKSource(componentRegistry);
    }

    private static class DelegatingRegisteredClientRepository implements RegisteredClientRepository {

        private final TenantPerIssuerComponentRegistry componentRegistry;

        private DelegatingRegisteredClientRepository(TenantPerIssuerComponentRegistry componentRegistry) {
            this.componentRegistry = componentRegistry;
        }

        @Override
        public void save(RegisteredClient registeredClient) {
            getRegisteredClientRepository().save(registeredClient);
        }

        @Override
        public RegisteredClient findById(String id) {
            return getRegisteredClientRepository().findById(id);
        }

        @Override
        public RegisteredClient findByClientId(String clientId) {
            return getRegisteredClientRepository().findByClientId(clientId);
        }

        private RegisteredClientRepository getRegisteredClientRepository() {
            RegisteredClientRepository registeredClientRepository =
                    this.componentRegistry.get(RegisteredClientRepository.class);
            Assert.state(registeredClientRepository != null,
                    "RegisteredClientRepository not found for \"requested\" issuer identifier.");
            return registeredClientRepository;
        }

    }

    private static class DelegatingOAuth2AuthorizationService implements OAuth2AuthorizationService {

        private final TenantPerIssuerComponentRegistry componentRegistry;

        private DelegatingOAuth2AuthorizationService(TenantPerIssuerComponentRegistry componentRegistry) {
            this.componentRegistry = componentRegistry;
        }

        @Override
        public void save(OAuth2Authorization authorization) {
            getAuthorizationService().save(authorization);
        }

        @Override
        public void remove(OAuth2Authorization authorization) {
            getAuthorizationService().remove(authorization);
        }

        @Override
        public OAuth2Authorization findById(String id) {
            return getAuthorizationService().findById(id);
        }

        @Override
        public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
            return getAuthorizationService().findByToken(token, tokenType);
        }

        private OAuth2AuthorizationService getAuthorizationService() {
            OAuth2AuthorizationService authorizationService =
                    this.componentRegistry.get(OAuth2AuthorizationService.class);
            Assert.state(authorizationService != null,
                    "OAuth2AuthorizationService not found for \"requested\" issuer identifier.");
            return authorizationService;
        }
    }

    private static class DelegatingOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

        private final TenantPerIssuerComponentRegistry componentRegistry;

        private DelegatingOAuth2AuthorizationConsentService(TenantPerIssuerComponentRegistry componentRegistry) {
            this.componentRegistry = componentRegistry;
        }

        @Override
        public void save(OAuth2AuthorizationConsent authorizationConsent) {
            getAuthorizationConsentService().save(authorizationConsent);
        }

        @Override
        public void remove(OAuth2AuthorizationConsent authorizationConsent) {
            getAuthorizationConsentService().remove(authorizationConsent);
        }

        @Override
        public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
            return getAuthorizationConsentService().findById(registeredClientId, principalName);
        }

        private OAuth2AuthorizationConsentService getAuthorizationConsentService() {
            OAuth2AuthorizationConsentService authorizationConsentService =
                    this.componentRegistry.get(OAuth2AuthorizationConsentService.class);
            Assert.state(authorizationConsentService != null,
                    "OAuth2AuthorizationConsentService not found for \"requested\" issuer identifier.");
            return authorizationConsentService;
        }

    }

    private static class DelegatingJWKSource implements JWKSource<SecurityContext> {

        private final TenantPerIssuerComponentRegistry componentRegistry;

        private DelegatingJWKSource(TenantPerIssuerComponentRegistry componentRegistry) {
            this.componentRegistry = componentRegistry;
        }

        @Override
        public List<JWK> get(JWKSelector jwkSelector, SecurityContext context) {
            return jwkSelector.select(getJwkSet());
        }

        private JWKSet getJwkSet() {
            JWKSet jwkSet = this.componentRegistry.get(JWKSet.class);
            Assert.state(jwkSet != null, "JWKSet not found for \"requested\" issuer identifier.");
            return jwkSet;
        }
    }

}
