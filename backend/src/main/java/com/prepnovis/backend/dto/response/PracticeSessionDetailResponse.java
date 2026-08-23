package com.prepnovis.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.PracticeSessionStatus;
import com.prepnovis.backend.entity.enums.QuestionType;

public class PracticeSessionDetailResponse {

    private UUID id;
    private String category;
    private String topic;
    private DifficultyLevel difficultyLevel;
    private QuestionType questionType;
    private Integer totalQuestions;
    private PracticeSessionStatus status;
    private LocalDateTime createdAt;
    private List<PracticeSessionQuestionResponse> questions;

    public PracticeSessionDetailResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public PracticeSessionStatus getStatus() {
        return status;
    }

    public void setStatus(PracticeSessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PracticeSessionQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PracticeSessionQuestionResponse> questions) {
        this.questions = questions;
    }
}