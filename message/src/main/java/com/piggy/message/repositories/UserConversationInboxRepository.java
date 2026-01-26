package com.piggy.message.repositories;

import com.piggy.message.models.UserConversationInbox;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserConversationInboxRepository extends MongoRepository<UserConversationInbox, String> {
    List<UserConversationInbox> findByUserIdAndArchivedFalseOrderByPinnedDescLastMessageTimestampDescConversationIdDesc(
            UUID userId,
            Limit limit
    );
    List<UserConversationInbox> findByUserIdAndArchivedFalseAndLastMessageTimestampLessThanOrLastMessageTimestampEqualsAndConversationIdLessThanOrderByPinnedDescLastMessageTimestampDescConversationIdDesc(
            UUID userId,
            Instant lastMessageTimestamp,
            Instant equalTimestamp,
            String conversationId,
            Limit limit
    );

    Optional<UserConversationInbox> findByUserIdAndConversationId(UUID userId, String conversationId);
}
