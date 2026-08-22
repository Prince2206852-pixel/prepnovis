package com.prepnovis.backend.service;


import java.util.UUID;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.PageResponse;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;


public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    PageResponse<QuestionResponse> getAllQuestions(
        int page,
        int size,
        String category,
        String topic,
        DifficultyLevel difficultyLevel,
        QuestionType questionType);
        
    QuestionResponse getQuestionById(UUID id);

    QuestionResponse updateQuestion(UUID id, QuestionRequest request);

    void deleteQuestion(UUID id);
}