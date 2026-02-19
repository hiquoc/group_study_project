package com.piggy.message.services.others;

import com.piggy.message.models.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class Scheduler {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRY = 10;

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxEvents() {

        for (int i = 0; i < BATCH_SIZE; i++) {

            OutboxEvent event = claimNextEvent();

            if (event == null) {
                return;
            }

            processEvent(event);
        }
    }

    private OutboxEvent claimNextEvent() {

        Query query = new Query(
                Criteria.where("status").is("NEW")
        ).with(Sort.by(Sort.Direction.ASC, "created_at"));

        Update update = new Update()
                .set("status", "PROCESSING");

        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                OutboxEvent.class
        );
    }

    private void processEvent(OutboxEvent event) {

        try {
            kafkaTemplate.send(
                    event.getEventType(),
                    event.getAggregateId(),
                    event.getPayload()
            ).get();

            event.setStatus("SENT");
            event.setProcessedAt(Instant.now());

        } catch (Exception ex) {

            int retry = event.getRetryCount() + 1;
            event.setRetryCount(retry);

            if (retry >= MAX_RETRY) {
                event.setStatus("FAILED");
            } else {
                event.setStatus("NEW"); // retry later
            }
        }

        mongoTemplate.save(event);
    }

    /**
     * Cleanup old SENT events (runs every hour)
     */
    @Scheduled(fixedDelay = 3600000)
    public void cleanupSentEvents() {

        Instant threshold = Instant.now().minusSeconds(7 * 24 * 3600);

        mongoTemplate.remove(
                Query.query(
                        Criteria.where("status").is("SENT")
                                .and("processed_at").lt(threshold)
                ),
                OutboxEvent.class
        );
    }
}
