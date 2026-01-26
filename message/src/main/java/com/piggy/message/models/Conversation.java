package com.piggy.message.models;

import com.piggy.message.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
@Builder
public class Conversation {

    @Id
    private String id;

    @Field("type")
    private ConversationType type;

    @Field("group_id")
    @Indexed(unique = true, sparse = true)
    private UUID groupId;

    @Field("members_hash")
    @Indexed(unique = true, sparse = true)
    private String membersHash;

    @Field("display_name")
    private String displayName;

    @Field("display_avatar")
    private String displayAvatar;

    @Field("is_deleted")
    private boolean isDeleted = false;
}
