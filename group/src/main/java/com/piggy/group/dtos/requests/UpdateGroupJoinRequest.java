package com.piggy.group.dtos.requests;

import com.piggy.group.enums.GroupJoinRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupJoinRequest {
    @NotNull(message = "Status must not be null")
    private GroupJoinRequestStatus status;
}

