package com.piggy.chat.dtos.requests;

import com.piggy.chat.enums.GroupJoinRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateGroupJoinRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID groupId;
}

