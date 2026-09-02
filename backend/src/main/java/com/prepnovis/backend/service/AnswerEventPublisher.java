package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.event.AnswerEvaluatedEvent;

public interface AnswerEventPublisher {

    void publishAnswerEvaluatedEvent(
            AnswerEvaluatedEvent event
    );
}