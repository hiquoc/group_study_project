package com.piggy.message.repositories;

import com.piggy.message.models.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    boolean existsByGroupId(UUID groupId);

    Optional<Conversation> findByGroupId(UUID groupId);

    Optional<Conversation> findByMembersHash(String membersHash);
}
