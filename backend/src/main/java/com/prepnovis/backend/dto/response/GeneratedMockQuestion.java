package com.prepnovis.backend.dto.response;

public class GeneratedMockQuestion {

    private String questionText;
    private String referenceAnswer;

    public GeneratedMockQuestion() {
    }

    public GeneratedMockQuestion(
            String questionText,
            String referenceAnswer) {

        this.questionText = questionText;
        this.referenceAnswer = referenceAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getReferenceAnswer() {
        return referenceAnswer;
    }

    public void setReferenceAnswer(String referenceAnswer) {
        this.referenceAnswer = referenceAnswer;
    }
}