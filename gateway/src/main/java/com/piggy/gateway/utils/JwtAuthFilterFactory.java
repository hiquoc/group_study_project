package com.piggy.gateway.utils;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component("JwtAuth")
public class JwtAuthFilterFactory extends AbstractGatewayFilterFactory<Object> {
    private final JwtUtil jwtUtil;
    public JwtAuthFilterFactory(JwtUtil jwtUtil){
        super(Object.class);
        this.jwtUtil=jwtUtil;
    }
    @Override
    public GatewayFilter apply(Object config) {
        return new JwtAuthFilter(jwtUtil);
    }
}
