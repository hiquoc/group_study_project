package com.piggy.message.repositories;

import com.piggy.message.models.Message;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConversationIdAndCreatedAtLessThan(String conversationId, Instant cursor, Pageable pageable);
}
