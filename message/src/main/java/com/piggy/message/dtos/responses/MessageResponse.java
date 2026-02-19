package com.piggy.message.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String displayName;
    private String displayAvatar;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted;
}
