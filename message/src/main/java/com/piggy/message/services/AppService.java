package com.piggy.message.services;

import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.enums.ConversationType;
import com.piggy.message.exceptions.BadRequestException;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Conversation;
import com.piggy.message.models.Message;
import com.piggy.message.models.UserConversationInbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppService {
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final ConversationMemberService conversationMemberService;
    private final UserConversationInboxService userConversationInboxService;

    //Message
    public Message sendPrivateMessage(UUID senderId, String content, String conversationId, UUID receiverId) {
        Conversation conversation;
        if (conversationId != null) {
            conversation = conversationService.findById(conversationId)
                    .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
            conversationMemberService.checkIfUserBelongToConversation(conversationId, senderId);

        } else if (receiverId != null) {
            conversation = conversationService.findByMembersHash(senderId, receiverId)
                    .orElseGet(() -> {
                        Conversation c = conversationService.createPrivateConversation(senderId, receiverId);

                        conversationMemberService.addConversationMember(
                                c.getId(),
                                List.of(
                                        new MembersData(senderId, "USER"),
                                        new MembersData(receiverId, "USER")
                                )
                        );
                        return c;
                    });
        } else {
            throw new BadRequestException("conversationId or receiverId is required");
        }

        Message message = messageService.sendMessage(senderId, content, conversation.getId());

        UUID targetId = receiverId != null
                ? receiverId
                : conversationService.getOtherMemberId(conversation.getId(), senderId);

        if (targetId == null) {
            throw new BadRequestException("Private conversation has no other member");
        }
        userConversationInboxService.onNewMessage(senderId, conversation.getId(),
                message.getId(), Collections.singletonList(targetId));

        return message;
    }
    public Message sendGroupMessage(UUID senderId, String content, String conversationId) {
        if(conversationId == null){
            throw new BadRequestException("ConversationId is required");
        }
        Conversation conversation=conversationService.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationMemberService.checkIfUserBelongToConversation(conversationId, senderId);
        Message message=messageService.sendMessage(senderId, content, conversation.getId());

        List<UUID> receiverIds =conversationMemberService.getMemberIds(conversationId).stream()
                        .filter(id->!id.equals(senderId))
                                .toList();
        userConversationInboxService.onNewMessage(senderId,conversationId,message.getId(), receiverIds);
        return message;
    }

    //Inbox
    public List<UserConversationInbox> getRecentConversations(UUID userId, Instant cursorTimestamp,
                                                              String cursorConversationId, int limit) {
        return userConversationInboxService.getRecentConversations(userId, cursorTimestamp, cursorConversationId, limit);
    }

    //Conversation
    public Conversation createConversation(ConversationType type, UUID groupId,
                                           String displayName, String displayAvatar,
                                           List<MembersData> membersDataList) {
        if (type == ConversationType.PRIVATE) {
            if (membersDataList.size() != 2)
                throw new BadRequestException("Private conversation must have exactly 2 members");

            UUID userId1 = membersDataList.get(0).getMemberId();
            UUID userId2 = membersDataList.get(1).getMemberId();

            Optional<Conversation> existing = conversationService.findByMembersHash(userId1, userId2);

            if (existing.isPresent())
                return existing.get();

            Conversation conversation = conversationService.createPrivateConversation(userId1, userId2);

            conversationMemberService.addConversationMember(conversation.getId(), membersDataList);


            return conversation;
        }

        // GROUP
        if (groupId == null)
            throw new BadRequestException("GroupId is required");

        Optional<Conversation> existing = conversationService.findByGroupId(groupId);

        if (existing.isPresent()) {
            conversationMemberService.addConversationMember(existing.get().getId(), membersDataList);
            return existing.get();
        }

        Conversation conversation = conversationService.createGroupConversation(groupId, displayName, displayAvatar);
        conversationMemberService.addConversationMember(conversation.getId(), membersDataList);
        return conversation;
    }

    public void deleteGroupConversation(UUID groupId) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationService.deleteConversation(conversation.getId());
        conversationMemberService.removeAllConversationMember(conversation.getId());
    }

    public void deletePrivateConversation(UUID user1Id, UUID user2Id) {
        Conversation conversation = conversationService.findByMembersHash(user1Id, user2Id)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        conversationMemberService.removeAllConversationMember(conversation.getId());
        conversationService.deleteConversation(conversation.getId());
    }

    //Conversation Member
    public void addConversationMemberWhenNewUserEnterGroup(UUID groupId, MembersData membersData) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
        conversationMemberService.addConversationMember(conversation.getId(), List.of(membersData));
    }

    public void removeConversationMemberWhenUserLeaveGroup(UUID groupId, UUID memberId) {
        Conversation conversation = conversationService.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Conversation Not Found"));
        conversationMemberService.removeConversationMember(conversation.getId(), memberId);
    }

}
