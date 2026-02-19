package com.piggy.message.dtos.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSentEvent {

    private String messageId;
    private String conversationId;
    private UUID senderId;
    private String content;
    private Instant createdAt;
}
