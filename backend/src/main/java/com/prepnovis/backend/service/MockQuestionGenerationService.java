package com.prepnovis.backend.service;

import java.util.List;

import com.prepnovis.backend.dto.response.GeneratedMockQuestion;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

public interface MockQuestionGenerationService {

    List<GeneratedMockQuestion> generateQuestions(
            String category,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionType questionType,
            int totalQuestions
    );
}