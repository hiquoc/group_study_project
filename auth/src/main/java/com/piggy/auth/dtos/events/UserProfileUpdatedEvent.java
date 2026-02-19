package com.piggy.auth.dtos.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileUpdatedEvent {
    private UUID userId;
    private String username;
    private String avatarUrl;
}
