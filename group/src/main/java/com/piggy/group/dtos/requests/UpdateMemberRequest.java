package com.piggy.group.dtos.requests;

import com.piggy.group.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {
    @NotNull(message = "New role must not be emty")
    private GroupRole newRole;
}
