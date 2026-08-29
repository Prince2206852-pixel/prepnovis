package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.response.AnswerEvaluationResult;

public interface AnswerEvaluationService {

    AnswerEvaluationResult evaluateAnswer(
            String questionText,
            String referenceAnswer,
            String userAnswer
    );
}