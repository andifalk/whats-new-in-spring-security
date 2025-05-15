package com.example.onetimetoken.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.JdbcOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ott.DefaultGenerateOneTimeTokenRequestResolver;
import org.springframework.security.web.authentication.ott.GenerateOneTimeTokenRequestResolver;

import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests.anyRequest().authenticated()
                )
                .oneTimeTokenLogin(withDefaults())
                .headers(h -> h.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable) //Only for the H2 database console (not production ready)
                )
                .csrf(AbstractHttpConfigurer::disable); //Only for the H2 database console (not production ready
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("test@example.com")
                        .password("secret")
                        .passwordEncoder(passwordEncoder()::encode)
                        .roles("USER").build()
        );
    }

    // Configure the One Time Token service to use a JDBC-backed implementation (production ready)
    @Bean
    public OneTimeTokenService oneTimeTokenService(JdbcTemplate jdbcTemplate) {
        return new JdbcOneTimeTokenService(jdbcTemplate);
    }

    // Customize the One Time Token request resolver to increase the expiry time (new in Spring Security 6.5)
    @Bean
    GenerateOneTimeTokenRequestResolver generateOneTimeTokenRequestResolver() {
        DefaultGenerateOneTimeTokenRequestResolver delegate = new DefaultGenerateOneTimeTokenRequestResolver();
        return (request) -> {
            GenerateOneTimeTokenRequest generate = delegate.resolve(request);
            if (generate == null) {
                return null;
            }
            // Increase expiry to 10 minutes (default is 5 minutes)
            return new GenerateOneTimeTokenRequest(generate.getUsername(), Duration.ofSeconds(600));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
