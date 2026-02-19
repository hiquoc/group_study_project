package com.piggy.message.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "user_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache {

    @Id
    @Field("user_id")
    private UUID userId;

    private String username;
    @Field("avatar_url")
    private String avatarUrl;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
}
