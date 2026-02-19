package com.piggy.message.services.others;

import com.piggy.message.dtos.events.*;
import com.piggy.message.services.UserCacheService;
import com.piggy.message.services.UserConversationInboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final UserConversationInboxService inboxService;
    private final UserCacheService userCacheService;

    @KafkaListener(topics = "message.sent",
            concurrency = "3")
    public void handleMessageSent(MessageSentEvent event) {
        inboxService.onNewMessage(
                event.getSenderId(),
                event.getConversationId(),
                event.getMessageId(),
                event.getCreatedAt());
    }

    @KafkaListener(topics = "private.conversation.created")
    public void handlePrivateConversationCreated(PrivateConversationCreatedEvent event) {
        inboxService.createInboxesForPrivateConversation(
                event.getConversationId(), event.getUserA(), event.getUserB()
        );
    }

    @KafkaListener(topics = "group.conversation.created")
    public void handleGroupConversationCreated(UserJoinGroupEvent event) {
        inboxService.createInboxForAUserInGroupChat(
                event.getConversationId(), event.getDisplayName(), event.getDisplayAvatar(),
                event.getUserId(), event.getLastMessageId(), event.getLastMessageTimestamp()
        );
    }

    @KafkaListener(topics = "group.conversation.joined")
    public void handleUserJoinGroup(UserJoinGroupEvent event) {
        inboxService.createInboxForAUserInGroupChat(
                event.getConversationId(), event.getDisplayName(), event.getDisplayAvatar(),
                event.getUserId(), event.getLastMessageId(), event.getLastMessageTimestamp()
        );
    }

    @KafkaListener(topics = "group.conversation.left")
    public void handleUserLeftGroup(UserLeftGroupEvent event) {
        inboxService.deleteInbox(event.getConversationId(), event.getUserId());
    }

    @KafkaListener(topics = "user.updated")
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        userCacheService.updateUserCache(event.getUserId(), event.getUsername(), event.getAvatarUrl());
    }
}
