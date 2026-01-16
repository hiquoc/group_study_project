package com.piggy.chat.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateGroupRequest {
    @NotBlank(message = "Group name must not be blank")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;

    private String avatarUrl;
    private Boolean isPrivate = false;
}
