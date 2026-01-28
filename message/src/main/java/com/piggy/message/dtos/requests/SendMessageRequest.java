package com.piggy.message.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    @NotBlank(message = "Content is required")
    private String content;
    private String conversationId;
    private UUID receiverId;
}
