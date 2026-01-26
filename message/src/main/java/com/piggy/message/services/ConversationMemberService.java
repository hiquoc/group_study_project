package com.piggy.message.services;

import com.mongodb.DuplicateKeyException;
import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.ConversationMember;
import com.piggy.message.repositories.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationMemberService {
    private final ConversationMemberRepository conversationMemberRepository;
    private final MongoTemplate mongoTemplate;

    public ConversationMember findByConversationIdAndUserId(String conversationId, UUID userId) {
        return conversationMemberRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new NotFoundException("User is not in this conversation"));
    }

    public void addConversationMember(String conversationId, List<MembersData> membersDataList) {
        List<ConversationMember> pendingSave = new ArrayList<>();
        for (MembersData membersData : membersDataList) {
            ConversationMember conversationMember = ConversationMember.builder()
                    .conversationId(conversationId)
                    .userId(membersData.getMemberId())
                    .role(membersData.getRole())
                    .build();
            pendingSave.add(conversationMember);
        }
        try {
            conversationMemberRepository.saveAll(pendingSave);
        } catch (DuplicateKeyException e) {
//          Ignore duplicates — this is expected behavior
        }
    }

    public ConversationMember updateConversationMember(String conversationId, UUID userId, String role) {
        ConversationMember conversationMember = findByConversationIdAndUserId(conversationId, userId);
        conversationMember.setRole(role);
        return conversationMemberRepository.save(conversationMember);
    }

    public void removeConversationMember(String conversationId, UUID userId) {
        conversationMemberRepository.deleteByConversationIdAndUserId(conversationId, userId);
    }
    public void removeAllConversationMember(String conversationId) {
        conversationMemberRepository.deleteByConversationId(conversationId);
    }
    public void checkIfUserBelongToConversation(String conversationId, UUID userId) {
        if(! conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId))
            throw new NotFoundException("User is not belong to this conversation");
    }
    public List<UUID> getMemberIds(String conversationId) {
        Query query = Query.query(Criteria.where("conversation_id").is(conversationId));
        query.fields().include("user_id");

        List<ConversationMember> members =
                mongoTemplate.find(query, ConversationMember.class);

        return members.stream()
                .map(ConversationMember::getUserId)
                .toList();
    }
}
