package com.piggy.message.services;

import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.UserConversationInbox;
import com.piggy.message.repositories.UserConversationInboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserConversationInboxService {
    private final UserConversationInboxRepository inboxRepository;
    private final MongoTemplate mongoTemplate;

    public List<UserConversationInbox> getRecentConversations(UUID userId, Instant cursorTimestamp,
                                                              String cursorConversationId, int limit) {
        if (cursorTimestamp == null) {
            return inboxRepository
                    .findByUserIdAndArchivedFalseOrderByPinnedDescLastMessageTimestampDescConversationIdDesc(
                            userId,
                            Limit.of(limit)
                    );
        }

        return inboxRepository
                .findByUserIdAndArchivedFalseAndLastMessageTimestampLessThanOrLastMessageTimestampEqualsAndConversationIdLessThanOrderByPinnedDescLastMessageTimestampDescConversationIdDesc(
                        userId,
                        cursorTimestamp,
                        cursorTimestamp,
                        cursorConversationId,
                        Limit.of(limit)
                );
    }
    public void onNewMessage(UUID senderId, String conversationId, String messageId, List<UUID> receiverIds) {
        Instant now = Instant.now();
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserConversationInbox.class);

        // Sender (no unread increment)
        bulkOps.upsert(
                Query.query(Criteria.where("userId").is(senderId)
                        .and("conversationId").is(conversationId)),
                new Update()
                        .setOnInsert("userId", senderId)
                        .setOnInsert("conversationId", conversationId)
                        .set("lastMessageId", messageId)
                        .set("lastMessageTimestamp", now)
        );

        // Receivers
        for (UUID receiverId : receiverIds) {
//            if (receiverId.equals(senderId)) continue;

            bulkOps.upsert(
                    Query.query(Criteria.where("userId").is(receiverId)
                            .and("conversationId").is(conversationId)),
                    new Update()
                            .setOnInsert("userId", receiverId)
                            .setOnInsert("conversationId", conversationId)
                            .set("lastMessageId", messageId)
                            .set("lastMessageTimestamp", now)
                            .inc("unreadCount", 1)
            );
        }

        bulkOps.execute();
    }



    private UserConversationInbox findByUserIdAndConversationId(UUID userId, String conversationId) {
        return inboxRepository.findByUserIdAndConversationId(userId, conversationId)
                .orElseThrow(()->new NotFoundException("UserConversationInbox not found"));
    }
}
