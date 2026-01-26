package com.piggy.message.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@Document(collection = "conversation_members")
@CompoundIndex(
        name = "unique_conversation_user",
        def = "{'conversation_id': 1, 'user_id': 1}",
        unique = true
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMember {

    @Id
    private String id;

    @Field("conversation_id")
    @Indexed
    private String conversationId;

    @Field("user_id")
    @Indexed
    private UUID userId;

    private String role;
}



