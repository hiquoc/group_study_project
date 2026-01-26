package com.piggy.message.dtos.others;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersData {
    @NotBlank(message = "Member id is required")
    private UUID memberId;
    @NotBlank(message = "Role is required")
    private String role;
}
