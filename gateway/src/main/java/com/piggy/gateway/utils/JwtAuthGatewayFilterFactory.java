package com.piggy.gateway.utils;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthGatewayFilterFactory
        extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

//            log.debug(">>> JwtAuthFilter ENTERED");

            String header = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (header == null || !header.startsWith("Bearer ")) {
                log.debug(">>> No Authorization header");
                return chain.filter(exchange);
            }

            try {
                String token = header.substring(7);
                Claims claims = jwtUtil.parseToken(token);

                ServerHttpRequest mutated =
                        exchange.getRequest().mutate()
                                .header("X-Account-Id", claims.getSubject())
                                .header("X-User-Id", claims.get("userId", String.class))
                                .header("X-Role", claims.get("role", String.class))
                                .build();

//                log.debug(">>> JwtAuthFilter passed");

                return chain.filter(
                        exchange.mutate().request(mutated).build()
                );
            } catch (Exception e) {
                log.warn("JWT invalid", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}

