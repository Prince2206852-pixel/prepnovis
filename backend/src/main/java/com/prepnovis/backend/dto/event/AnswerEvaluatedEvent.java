package com.prepnovis.backend.dto.event;

import java.util.UUID;

import com.prepnovis.backend.entity.QuestionSource;

public class AnswerEvaluatedEvent {

    private UUID userId;
    private UUID sessionId;
    private UUID sessionQuestionId;
    private QuestionSource questionSource;
    private Double score;
    private String evaluatedAt;

    public AnswerEvaluatedEvent() {
    }

    public AnswerEvaluatedEvent(
            UUID userId,
            UUID sessionId,
            UUID sessionQuestionId,
            QuestionSource questionSource,
            Double score,
            String evaluatedAt) {

        this.userId = userId;
        this.sessionId = sessionId;
        this.sessionQuestionId = sessionQuestionId;
        this.questionSource = questionSource;
        this.score = score;
        this.evaluatedAt = evaluatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionQuestionId() {
        return sessionQuestionId;
    }

    public void setSessionQuestionId(UUID sessionQuestionId) {
        this.sessionQuestionId = sessionQuestionId;
    }

    public QuestionSource getQuestionSource() {
        return questionSource;
    }

    public void setQuestionSource(QuestionSource questionSource) {
        this.questionSource = questionSource;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(String evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }
}