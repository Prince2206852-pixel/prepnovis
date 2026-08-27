package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.response.AnswerEvaluationResult;
import com.prepnovis.backend.entity.Question;

public interface AnswerEvaluationService {

    AnswerEvaluationResult evaluateAnswer(
            Question question,
            String userAnswer
    );
}