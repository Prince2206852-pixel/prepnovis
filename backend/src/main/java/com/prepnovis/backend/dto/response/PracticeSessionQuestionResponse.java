package com.prepnovis.backend.dto.response;

import java.util.UUID;

public class PracticeSessionQuestionResponse {

    private UUID id;
    private UUID questionId;
    private String questionText;
    private String category;
    private String topic;
    private String questionType;
    private String difficultyLevel;
    private Boolean answered;
    private String userAnswer;
    private Double score;
    private String feedback;
    private String strengths;
    private String improvements;

    public PracticeSessionQuestionResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public String getStrengths() {
    return strengths;
}

public void setStrengths(String strengths) {
    this.strengths = strengths;
}

public String getImprovements() {
    return improvements;
}

public void setImprovements(String improvements) {
    this.improvements = improvements;
}

}