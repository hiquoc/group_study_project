package com.piggy.chat.dtos.requests;

import com.piggy.chat.enums.GroupJoinRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateGroupJoinRequest {
    @NotNull(message = "Status must not be null")
    private GroupJoinRequestStatus status;
}

