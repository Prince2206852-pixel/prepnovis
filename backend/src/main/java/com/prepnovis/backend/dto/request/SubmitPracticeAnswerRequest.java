package com.prepnovis.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitPracticeAnswerRequest {

    @NotBlank(message = "Answer is required")
    @Size(max = 10000, message = "Answer must not exceed 10000 characters")
    private String answer;

    public SubmitPracticeAnswerRequest() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}