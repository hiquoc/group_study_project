package com.piggy.message.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserConversationInboxResponse {
    private UUID userId;
    private String conversationId;
    private String displayName;
    private String displayAvatar;
    private String lastMessage;
    private Instant lastMessageTimestamp;
    private boolean pinned = false;
    private int unreadCount = 0;
    private boolean muted = false;
    private boolean archived = false;
}
