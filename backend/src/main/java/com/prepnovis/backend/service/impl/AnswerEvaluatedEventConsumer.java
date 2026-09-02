package com.prepnovis.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.event.AnswerEvaluatedEvent;

@Service
public class AnswerEvaluatedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(AnswerEvaluatedEventConsumer.class);

    @KafkaListener(
            topics = "answer-evaluated",
            groupId = "prepnovis-answer-events"
    )
    public void consume(
            AnswerEvaluatedEvent event) {

        log.info(
                "Received answer evaluated event. userId={}, sessionId={}, sessionQuestionId={}, score={}",
                event.getUserId(),
                event.getSessionId(),
                event.getSessionQuestionId(),
                event.getScore()
        );
    }
}