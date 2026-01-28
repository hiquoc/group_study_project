package com.piggy.message.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthUser {
    private final UUID userId;
    private final String role;
}