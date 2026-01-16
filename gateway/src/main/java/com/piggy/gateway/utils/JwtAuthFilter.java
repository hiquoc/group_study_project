package com.piggy.gateway.utils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GatewayFilter {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
        String header=request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(header==null||!header.startsWith(("Bearer ")))
            return chain.filter(exchange);
        String token=header.substring(7);
        try{
            Claims claims= jwtUtil.parseToken(token);
            String accountId=claims.getSubject();
            String role=claims.get("role",String.class);
            String userId=claims.get("userId",String.class);

            ServerHttpRequest mutatedRequest=request.mutate()
                    .header("X-Account-Id", accountId)
                    .header("X-User-Id", userId)
                    .header("X-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }
        catch (Exception e){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
