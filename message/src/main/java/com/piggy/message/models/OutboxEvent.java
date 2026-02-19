package com.piggy.message.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "out_box_events")
@CompoundIndex(
        name = "idx_status_createdAt",
        def = "{'status': 1, 'created_at': 1}"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    private String id;

    // message.sent, group.conversation.created, etc.
    @Field(name = "event_type")
    private String eventType;

    // Kafka key (conversationId, userId, etc.)
    @Field(name = "aggregate_id")
    private String aggregateId;

    @Field(name = "payload")
    private String payload;

    // NEW, SENT
    @Field(name = "status")
    private String status;

    // when event was created
    @Field(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Field("processed_at")
    private Instant processedAt;

    @Field(name = "retry_count")
    private Integer retryCount=0;

}
