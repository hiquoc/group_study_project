package com.piggy.message.dtos.events;

import com.piggy.message.models.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinGroupEvent {
    private String conversationId;
    private String displayName;
    private String displayAvatar;
    private UUID userId;
    private String lastMessageId;
    private Instant lastMessageTimestamp;
}
