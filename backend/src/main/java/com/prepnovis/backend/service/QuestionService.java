package com.prepnovis.backend.service;

import java.util.List;
import java.util.UUID;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    List<QuestionResponse> getAllQuestions();

    QuestionResponse getQuestionById(UUID id);

    QuestionResponse updateQuestion(UUID id, QuestionRequest request);

    void deleteQuestion(UUID id);
}