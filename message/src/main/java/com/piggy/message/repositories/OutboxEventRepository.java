package com.piggy.message.repositories;

import com.piggy.message.models.OutboxEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository extends MongoRepository<OutboxEvent, String> {
    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(String status);

    void deleteByStatusAndProcessedAtBefore(String status, Instant time);
}
