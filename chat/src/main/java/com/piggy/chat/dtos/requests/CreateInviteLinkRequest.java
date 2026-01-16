package com.piggy.chat.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateInviteLinkRequest {
    @NotNull(message = "Group id is required")
    private UUID groupId;
}
