package com.prepnovis.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.exception.QuestionNotFoundException;
import com.prepnovis.backend.repository.QuestionRepository;
import com.prepnovis.backend.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {

        Question question = new Question();

        question.setQuestionText(request.getQuestionText());
        question.setAnswer(request.getAnswer());
        question.setCategory(request.getCategory());
        question.setTopic(request.getTopic());
        question.setQuestionType(request.getQuestionType());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setTags(request.getTags());

        Question savedQuestion = questionRepository.save(question);

        return mapToResponse(savedQuestion);
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {

        return questionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public QuestionResponse getQuestionById(UUID id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new QuestionNotFoundException("Question not found.")
                );

        return mapToResponse(question);
    }

    @Override
    public QuestionResponse updateQuestion(
            UUID id,
            QuestionRequest request) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new QuestionNotFoundException("Question not found.")
                );

        question.setQuestionText(request.getQuestionText());
        question.setAnswer(request.getAnswer());
        question.setCategory(request.getCategory());
        question.setTopic(request.getTopic());
        question.setQuestionType(request.getQuestionType());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setTags(request.getTags());

        Question updatedQuestion = questionRepository.save(question);

        return mapToResponse(updatedQuestion);
    }

    @Override
    public void deleteQuestion(UUID id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new QuestionNotFoundException("Question not found.")
                );

        questionRepository.delete(question);
    }

    private QuestionResponse mapToResponse(Question question) {

        QuestionResponse response = new QuestionResponse();

        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setAnswer(question.getAnswer());
        response.setCategory(question.getCategory());
        response.setTopic(question.getTopic());
        response.setQuestionType(question.getQuestionType());
        response.setDifficultyLevel(question.getDifficultyLevel());
        response.setTags(question.getTags());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());

        return response;
    }
}