package com.piggy.auth.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Please enter old password")
    private String oldPassword;
    @NotBlank(message = "New password cant be empty")
    private String newPassword;
}
