package com.prepnovis.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.response.AnswerEvaluationResult;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.service.AnswerEvaluationService;

@Service
public class AnswerEvaluationServiceImpl
        implements AnswerEvaluationService {

    @Override
    public AnswerEvaluationResult evaluateAnswer(
            Question question,
            String userAnswer) {

        AnswerEvaluationResult result =
                new AnswerEvaluationResult();

        // Temporary evaluation logic.
        // This will be replaced by AI evaluation later.
        result.setScore(7.0);

        result.setFeedback(
                "Your answer has been submitted successfully. "
                + "AI-based evaluation will provide detailed feedback."
        );

        result.setStrengths(
                List.of("Answer provided successfully")
        );

        result.setImprovements(
                List.of("Detailed AI evaluation will be added")
        );

        return result;
    }
}