package com.piggy.message.repositories;

import com.piggy.message.models.ConversationMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends MongoRepository<ConversationMember, String> {
    void deleteByConversationIdAndUserId(String conversationId, UUID userId);

    Optional<ConversationMember> findByConversationIdAndUserId(String conversationId, UUID userId);

    void deleteByConversationId(String conversationId);

    boolean existsByConversationIdAndUserId(String conversationId, UUID userId);

}
