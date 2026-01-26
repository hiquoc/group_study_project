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

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_conversation_inboxes")
@CompoundIndex(
        name = "user_conversation_unique",
        def = "{ 'user_id': 1, 'conversation_id': 1 }",
        unique = true
)
@CompoundIndex(
        name = "user_inbox_sort",
        def = "{ 'user_id': 1, 'archived': 1, 'pinned': -1, 'last_message_timestamp': -1, 'conversation_id': -1 }"
)
@Builder
public class UserConversationInbox {

    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private UUID userId;

    @Field("conversation_id")
    private String conversationId;

    @Field("last_message_id")
    private String lastMessageId;

    @Field("last_message_timestamp")
    private Instant lastMessageTimestamp;

    @Field("pinned")
    private boolean pinned = false;

    @Field("unread_count")
    private int unreadCount = 0;

    @Field("muted")
    private boolean muted = false;

    @Field("archived")
    private boolean archived = false;
}
