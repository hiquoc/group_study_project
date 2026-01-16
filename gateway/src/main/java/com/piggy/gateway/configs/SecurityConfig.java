package com.piggy.gateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final String[] ALLOW_PATHS = {
            "/auth/login",
            "/auth/register"
    };
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception{
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(requests->requests
                        .pathMatchers(ALLOW_PATHS).permitAll()
                        .anyExchange().permitAll())
                .build();
    }

}
