package com.piggy.message.services;

import com.piggy.message.dtos.responses.UserConversationInboxResponse;
import com.piggy.message.dtos.responses.UserProfileResponse;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Message;
import com.piggy.message.models.UserConversationInbox;
import com.piggy.message.repositories.UserConversationInboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    public void createInboxesForPrivateConversation(String conversationId,
                                                    UserProfileResponse userA,
                                                    UserProfileResponse userB) {
        inboxRepository.saveAll(List.of(
                UserConversationInbox.builder()
                        .userId(userA.getId())
                        .conversationId(conversationId)
                        .displayName(userA.getUsername())
                        .displayAvatar(userA.getAvatar())
                        .unreadCount(0)
                        .build(),

                UserConversationInbox.builder()
                        .userId(userB.getId())
                        .conversationId(conversationId)
                        .displayName(userB.getUsername())
                        .displayAvatar(userB.getAvatar())
                        .unreadCount(0)
                        .build()
        ));
    }
    public void createInboxForAUserInGroupChat(String conversationId,
                                                  String displayName,
                                                  String displayAvatar,
                                                  UUID userId,
                                                  String lastMessageId,
                                                  Instant lastMessageTimestamp) {
        inboxRepository.save(
                UserConversationInbox.builder()
                        .userId(userId)
                        .conversationId(conversationId)
                        .displayName(displayName)
                        .displayAvatar(displayAvatar)
                        .unreadCount(0)
                        .lastMessageId(lastMessageId)
                        .lastMessageTimestamp(lastMessageTimestamp)
                        .build()
        );
    }

    public void onNewMessage(UUID senderId, String conversationId, String lastMessageId,
                             Instant lastMessageTimestamp) {
        // Sender (no unread increment)
        mongoTemplate.updateFirst(
                Query.query(
                        Criteria.where("userId").is(senderId)
                                .and("conversationId").is(conversationId)
                ),
                new Update()
                        .set("lastMessageId", lastMessageId)
                        .set("lastMessageTimestamp", lastMessageTimestamp)
                        .set("archived", false),
                UserConversationInbox.class
        );

        // Receivers
        mongoTemplate.updateMulti(
                Query.query(
                        Criteria.where("conversationId").is(conversationId)
                                .and("userId").nin(senderId)
                                .and("lastMessageId").ne(lastMessageId)
                                .orOperator(
                                        Criteria.where("lastMessageTimestamp").lt(lastMessageTimestamp),
                                        Criteria.where("lastMessageTimestamp").exists(false)
                                )
                ),
                new Update()
                        .set("lastMessageId", lastMessageId)
                        .set("lastMessageTimestamp", lastMessageTimestamp)
                        .inc("unreadCount", 1)
                        .set("archived", false),
                UserConversationInbox.class
        );
    }

    public void deleteInbox(String conversationId,UUID userId ) {
        inboxRepository.deleteByUserIdAndConversationId(userId,conversationId);
    }

    private UserConversationInbox findByUserIdAndConversationId(UUID userId, String conversationId) {
        return inboxRepository.findByUserIdAndConversationId(userId, conversationId)
                .orElseThrow(() -> new NotFoundException("UserConversationInbox not found"));
    }

    public UserConversationInboxResponse toResponse(
            UserConversationInbox inbox,
            Map<String, Message> messageMap
    ) {
        Message message = messageMap.get(inbox.getLastMessageId());

        return UserConversationInboxResponse.builder()
                .userId(inbox.getUserId())
                .conversationId(inbox.getConversationId())
                .displayName(inbox.getDisplayName())
                .displayAvatar(inbox.getDisplayAvatar())
                .lastMessage(message == null ? null : message.getContent())
                .lastMessageTimestamp(inbox.getLastMessageTimestamp())
                .pinned(inbox.isPinned())
                .unreadCount(inbox.getUnreadCount())
                .muted(inbox.isMuted())
                .archived(inbox.isArchived())
                .build();
    }

}

