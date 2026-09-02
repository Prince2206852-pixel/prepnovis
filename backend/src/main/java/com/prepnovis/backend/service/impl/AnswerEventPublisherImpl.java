package com.prepnovis.backend.service.impl;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.event.AnswerEvaluatedEvent;
import com.prepnovis.backend.service.AnswerEventPublisher;

@Service
public class AnswerEventPublisherImpl
        implements AnswerEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(AnswerEventPublisherImpl.class);

    private static final String TOPIC = "answer-evaluated";

    private final KafkaTemplate<String, AnswerEvaluatedEvent> kafkaTemplate;

    public AnswerEventPublisherImpl(
            KafkaTemplate<String, AnswerEvaluatedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
      public void publishAnswerEvaluatedEvent(
        AnswerEvaluatedEvent event) {

    String key = event.getUserId().toString();

    try {

        CompletableFuture<?> future =
                kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, exception) -> {

            if (exception != null) {

                log.error(
                        "Failed to publish answer evaluated event. sessionId={}, sessionQuestionId={}",
                        event.getSessionId(),
                        event.getSessionQuestionId(),
                        exception
                );

                return;
            }

            log.info(
                    "Published answer evaluated event. sessionId={}, sessionQuestionId={}",
                    event.getSessionId(),
                    event.getSessionQuestionId()
            );
        });

    } catch (RuntimeException exception) {

        log.error(
                "Kafka publish failed immediately. sessionId={}, sessionQuestionId={}. Answer processing will continue.",
                event.getSessionId(),
                event.getSessionQuestionId(),
                exception
        );
    }
}

}