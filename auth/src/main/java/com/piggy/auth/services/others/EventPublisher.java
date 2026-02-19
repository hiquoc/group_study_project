package com.piggy.auth.services.others;

import com.piggy.auth.dtos.events.UserProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserProfileUpdated(UserProfileUpdatedEvent event) {
        kafkaTemplate.send("user.updated", event.getUserId().toString(), event);
    }
}