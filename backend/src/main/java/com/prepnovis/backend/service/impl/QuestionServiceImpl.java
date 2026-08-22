package com.prepnovis.backend.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.PageResponse;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.exception.QuestionNotFoundException;
import com.prepnovis.backend.repository.QuestionRepository;
import com.prepnovis.backend.repository.specification.QuestionSpecification;
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
public PageResponse<QuestionResponse> getAllQuestions(
        int page,
        int size,
        String category,
        String topic,
        DifficultyLevel difficultyLevel,
        QuestionType questionType) {

    Pageable pageable = PageRequest.of(page, size);

    Specification<Question> specification =
            Specification
                    .where(QuestionSpecification.hasCategory(category))
                    .and(QuestionSpecification.hasTopic(topic))
                    .and(QuestionSpecification.hasDifficulty(difficultyLevel))
                    .and(QuestionSpecification.hasQuestionType(questionType));

    Page<Question> questionPage =
            questionRepository.findAll(specification, pageable);

    var content = questionPage.getContent()
            .stream()
            .map(this::mapToResponse)
            .toList();

    return new PageResponse<>(
            content,
            questionPage.getNumber(),
            questionPage.getSize(),
            questionPage.getTotalElements(),
            questionPage.getTotalPages(),
            questionPage.isFirst(),
            questionPage.isLast()
    );
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