package com.prepnovis.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.prepnovis.backend.entity.enums.PracticeSessionStatus;

public class PracticeSessionResultResponse {

    private UUID sessionId;
    private Integer totalQuestions;
    private Integer assignedQuestions;
    private Integer answeredQuestions;
    private Integer unansweredQuestions;
    private Double averageScore;
    private PracticeSessionStatus status;
    private LocalDateTime completedAt;

    public PracticeSessionResultResponse() {
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getAssignedQuestions() {
        return assignedQuestions;
    }

    public void setAssignedQuestions(Integer assignedQuestions) {
        this.assignedQuestions = assignedQuestions;
    }

    public Integer getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(Integer answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public Integer getUnansweredQuestions() {
        return unansweredQuestions;
    }

    public void setUnansweredQuestions(Integer unansweredQuestions) {
        this.unansweredQuestions = unansweredQuestions;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public PracticeSessionStatus getStatus() {
        return status;
    }

    public void setStatus(PracticeSessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}