package org.example.features.config;

import org.example.features.security.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ott.GeneratedOneTimeTokenHandler;

import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration {

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/v3/api-docs", "/swagger-ui.html", "/swagger-ui/**", "/h2-console")
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests.anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, GeneratedOneTimeTokenHandler generatedOneTimeTokenHandler) throws Exception {
        http.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests.anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(f -> f.defaultSuccessUrl("/api/accounts"))
                .oneTimeTokenLogin(ot -> ot.generatedOneTimeTokenHandler(generatedOneTimeTokenHandler));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                new User(Set.of("USER"), passwordEncoder().encode("secret"), "user", "Max", "Muster", "max.muster@example.com"),
                new User(Set.of("USER", "ACCOUNTANT"), passwordEncoder().encode("secret"), "accountant", "Andreas", "Accountant", "andreas.accountant@example.com"),
                new User(Set.of("USER", "ADMIN"), passwordEncoder().encode("secret"), "admin", "Andreas", "Admin", "andreas.admin@example.com")
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public static AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }

}
