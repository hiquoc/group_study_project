package com.piggy.message.services;

import com.piggy.message.dtos.events.MessageSentEvent;
import com.piggy.message.dtos.events.PrivateConversationCreatedEvent;
import com.piggy.message.dtos.events.UserJoinGroupEvent;
import com.piggy.message.dtos.events.UserLeftGroupEvent;
import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.dtos.responses.MessageResponse;
import com.piggy.message.dtos.responses.UserConversationInboxResponse;
import com.piggy.message.dtos.responses.UserProfileResponse;
import com.piggy.message.enums.ConversationType;
import com.piggy.message.exceptions.BadRequestException;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Conversation;
import com.piggy.message.models.Message;
import com.piggy.message.models.UserCache;
import com.piggy.message.models.UserConversationInbox;
import com.piggy.message.services.others.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppService {
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final ConversationMemberService conversationMemberService;
    private final UserConversationInboxService userConversationInboxService;
    private final UserCacheService userCacheService;
    private final OutboxEventService outboxEventService;

    private final UserServiceClient userServiceClient;
    private final SimpMessagingTemplate simpMessagingTemplate;

    //Message
    public List<MessageResponse> getMessages(UUID userId, String conversationId, Instant cursor, Integer limit) {
        conversationMemberService.checkIfUserBelongToConversation(conversationId, userId);
        List<Message> rawMessages = messageService.getMessages(conversationId, cursor, limit);

        return rawMessages.stream()
                .map(MessageResponse::toResponse)
                .toList();

    }

    public Message sendPrivateMessage(UUID senderId, String content, String conversationId, UUID receiverId) {
        Conversation conversation;
        if (conversationId != null) {
            conversation = conversationService.findById(conversationId)
                    .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
            conversationMemberService.checkIfUserBelongToConversation(conversationId, senderId);

        } else if (receiverId != null) {
            conversation = conversationService.findByMembersHash(senderId, receiverId)
                    .orElseGet(() -> createConversation(ConversationType.PRIVATE,
                            null, null, null,
                            List.of(
                                    new MembersData(senderId, "USER"),
                                    new MembersData(receiverId, "USER")
                            )));
        } else {
            throw new BadRequestException("conversationId or receiverId is required");
        }

        UUID targetId = receiverId != null
                ? receiverId
                : conversationService.getOtherMemberId(conversation.getId(), senderId);

        if (targetId == null) {
            throw new BadRequestException("Private conversation has no other member");
        }

        Message message = messageService.sendMessage(senderId, content, conversation.getId());

        MessageResponse messageResponse=MessageResponse.builder()
                .displayName(null)
                .displayAvatar(null)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .isDeleted(message.isDeleted())
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(targetId),"/queue/messages",messageResponse);

        MessageSentEvent event = new MessageSentEvent(
                message.getId(),
                conversation.getId(),
                senderId,
                content,
                message.getCreatedAt());

        outboxEventService.saveEvent("message.sent", conversation.getId(), event);

        return message;
    }

    public Message sendGroupMessage(UUID senderId, String content, String conversationId) {
        if (conversationId == null) {
            throw new BadRequestException("ConversationId is required");
        }
//        Conversation conversation = conversationService.findById(conversationId)
//                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationMemberService.checkIfUserBelongToConversation(conversationId, senderId);
        Message message = messageService.sendMessage(senderId, content, conversationId);


        simpMessagingTemplate.convertAndSend(
                "/topic/conversation/"+conversationId,MessageResponse.toResponse(message));

        MessageSentEvent event = new MessageSentEvent(
                message.getId(),
                conversationId,
                senderId,
                content,
                message.getCreatedAt()
        );
        outboxEventService.saveEvent("message.sent", conversationId, event);
        return message;
    }

    //Inbox
    public List<UserConversationInboxResponse> getRecentConversations(
            UUID userId,
            Instant cursorTimestamp,
            String cursorConversationId,
            Integer limit
    ) {
        List<UserConversationInbox> inboxes =
                userConversationInboxService.getRecentConversations(
                        userId, cursorTimestamp, cursorConversationId, limit
                );

        Set<String> messageIds = inboxes.stream()
                .map(UserConversationInbox::getLastMessageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Message> messageMap = messageIds.isEmpty()
                ? Map.of()
                : messageService.findAllById(messageIds)
                .stream()
                .collect(Collectors.toMap(Message::getId, Function.identity()));

        return inboxes.stream()
                .map(inbox -> userConversationInboxService.toResponse(inbox, messageMap))
                .toList();
    }


    //Conversation
    public Conversation createConversation(ConversationType type, UUID groupId,
                                           String displayName, String displayAvatar,
                                           List<MembersData> membersDataList) {
        Conversation conversation;
        if (type == ConversationType.PRIVATE) {
            if (membersDataList.size() != 2)
                throw new BadRequestException("Private conversation must have exactly 2 members");

            UUID senderId = membersDataList.get(0).getMemberId();
            UUID receiverId = membersDataList.get(1).getMemberId();

            Optional<Conversation> existing = conversationService.findByMembersHash(senderId, receiverId);

            if (existing.isPresent())
                return existing.get();

            conversation = conversationService.createPrivateConversation(senderId, receiverId);
            conversationMemberService.addConversationMember(conversation.getId(), membersDataList);

            List<UserProfileResponse> userProfileResponses = userServiceClient.getUsersProfile(List.of(senderId, receiverId));
            PrivateConversationCreatedEvent event = new PrivateConversationCreatedEvent(
                    conversation.getId(),
                    userProfileResponses.get(userProfileResponses.size() - 1),
                    userProfileResponses.get(0)
            );
            outboxEventService.saveEvent("private.conversation.created", conversation.getId(), event);
        } else {
            if (groupId == null)
                throw new BadRequestException("GroupId is required");

            Optional<Conversation> existing = conversationService.findByGroupId(groupId);

            if (existing.isPresent()) {
                conversationMemberService.addConversationMember(existing.get().getId(), membersDataList);
                return existing.get();
            }

            conversation = conversationService.createGroupConversation(groupId, displayName, displayAvatar);
            conversationMemberService.addConversationMember(conversation.getId(), membersDataList);

            UserJoinGroupEvent event = new UserJoinGroupEvent(
                    conversation.getId(),
                    conversation.getDisplayName(),
                    conversation.getDisplayAvatar(),
                    membersDataList.get(0).getMemberId(),
                    null,
                    null
            );
            outboxEventService.saveEvent("group.conversation.created", conversation.getId(), event);
        }
        return conversation;
    }

    public void deleteGroupConversation(UUID groupId) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationService.deleteConversation(conversation.getId());
        conversationMemberService.removeAllConversationMember(conversation.getId());
    }

    public void deletePrivateConversation(UUID requesterId, UUID userId) {
        Conversation conversation = conversationService.findByMembersHash(requesterId, userId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationMemberService.removeAllConversationMember(conversation.getId());
        conversationService.deleteConversation(conversation.getId());
    }

    //Conversation Member
    public void addConversationMemberWhenNewUserEnterGroup(UUID groupId, MembersData membersData) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
        conversationMemberService.addConversationMember(conversation.getId(), List.of(membersData));
        UserJoinGroupEvent event = new UserJoinGroupEvent(
                conversation.getId(),
                conversation.getDisplayName(),
                conversation.getDisplayAvatar(),
                membersData.getMemberId(),
                null,
                null
        );
        outboxEventService.saveEvent("group.conversation.joined", conversation.getId(), event);
    }

    public void removeConversationMemberWhenUserLeaveGroup(UUID groupId, UUID memberId) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
        conversationMemberService.removeConversationMember(conversation.getId(), memberId);
        UserLeftGroupEvent event = new UserLeftGroupEvent(conversation.getId(), memberId);
        outboxEventService.saveEvent("group.conversation.left", conversation.getId(), event);
    }
}
