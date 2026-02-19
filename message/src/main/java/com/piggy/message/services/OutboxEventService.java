package com.piggy.message.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggy.message.models.OutboxEvent;
import com.piggy.message.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(String status) {
        return repository.findTop100ByStatusOrderByCreatedAtAsc(status);
    }

    public void saveEvent(String type, String aggregateId, Object event) {
        try{
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .id(UUID.randomUUID().toString())
                    .eventType(type)
                    .aggregateId(aggregateId)
                    .payload(payload)
                    .status("NEW")
                    .build();

            repository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
