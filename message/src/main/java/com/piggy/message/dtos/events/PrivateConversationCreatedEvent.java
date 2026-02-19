package com.piggy.message.dtos.events;

import com.piggy.message.dtos.responses.UserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateConversationCreatedEvent {
    private String conversationId;
    private UserProfileResponse userA;
    private UserProfileResponse userB;
}
