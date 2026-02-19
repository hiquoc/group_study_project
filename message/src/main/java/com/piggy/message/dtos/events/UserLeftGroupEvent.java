package com.piggy.message.dtos.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserLeftGroupEvent {
    private String conversationId;
    private UUID userId;
}
