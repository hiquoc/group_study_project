package com.piggy.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class InternalGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public int getOrder() {
        return -100;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-Internal-Gateway");
                })
                .header("X-Internal-Gateway", "true")
                .build();
//        log.debug(">>> InternalGatewayFilter passed");
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
