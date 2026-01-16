package com.piggy.chat.dtos.requests;

import com.piggy.chat.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateMemberRequest {
    @NotNull(message = "New role must not be emty")
    private GroupRole newRole;
}
