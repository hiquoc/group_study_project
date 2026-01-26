package com.piggy.message.services;

import com.mongodb.DuplicateKeyException;
import com.piggy.message.enums.ConversationType;
import com.piggy.message.exceptions.ForbiddenException;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Conversation;
import com.piggy.message.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public Conversation getConversation(String conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Cant find conversation!"));
    }

    public Conversation createPrivateConversation(UUID userId1, UUID userId2) {
        Conversation conversation = Conversation.builder()
                .type(ConversationType.PRIVATE)
                .membersHash(buildMembersHash(userId1, userId2))
                .build();

        try {
            return conversationRepository.save(conversation);
        } catch (DuplicateKeyException e) {
            return findByMembersHash(userId1, userId2)
                    .orElseThrow(() -> e);
        }
    }
    public Conversation createGroupConversation(UUID groupId,String displayName,String displayAvatar) {
        Conversation conversation = Conversation.builder()
                .type(ConversationType.GROUP)
                .groupId(groupId)
                .displayName(displayName)
                .displayAvatar(displayAvatar)
                .build();

        try {
            return conversationRepository.save(conversation);
        } catch (DuplicateKeyException e) {
            return findByGroupId(groupId)
                    .orElseThrow(() -> e);
        }
    }


    public void deleteConversation(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        conversation.setDeleted(true);
        conversationRepository.save(conversation);
    }


    public Optional<Conversation> findByGroupId(UUID groupId) {
        return conversationRepository.findByGroupId(groupId);
    }

    public Optional<Conversation> findByMembersHash(UUID userId1, UUID userId2) {
        return conversationRepository.findByMembersHash(buildMembersHash(userId1,userId2));
    }
    public UUID getOtherMemberId(String conversationId, UUID userId) {
        Conversation conversation = getConversation(conversationId);
        String membersHash = conversation.getMembersHash();
        if (membersHash == null) {
            throw new ForbiddenException("Conversation is not a private conversation");
        }

        String[] parts = membersHash.split(":");
        UUID user1 = UUID.fromString(parts[0]);
        UUID user2 = UUID.fromString(parts[1]);

        if (userId.equals(user1)) {
            return user2;
        }
        if (userId.equals(user2)) {
            return user1;
        }

        throw new ForbiddenException("User is not a member of this conversation");
    }

    public Optional<Conversation> findById(String conversationId) {
        return conversationRepository.findById(conversationId);
    }

    private String buildMembersHash(UUID userId1, UUID userId2) {
        return userId1.compareTo(userId2) < 0 ?
                userId1 + ":" + userId2
                : userId2 + ":" + userId1;
    }

}
