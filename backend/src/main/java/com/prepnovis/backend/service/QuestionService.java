package com.prepnovis.backend.service;

import java.util.UUID;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.PageResponse;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

public interface QuestionService {

    QuestionResponse createQuestion(
            String email,
            QuestionRequest request
    );

    PageResponse<QuestionResponse> getAllQuestions(
            String email,
            int page,
            int size,
            String category,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionType questionType
    );

    QuestionResponse getQuestionById(
            String email,
            UUID id
    );

    QuestionResponse updateQuestion(
            String email,
            UUID id,
            QuestionRequest request
    );

    void deleteQuestion(
            String email,
            UUID id
    );
}