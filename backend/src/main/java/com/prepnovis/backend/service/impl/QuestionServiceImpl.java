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
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.exception.QuestionNotFoundException;
import com.prepnovis.backend.exception.UserNotFoundException;
import com.prepnovis.backend.repository.QuestionRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.repository.specification.QuestionSpecification;
import com.prepnovis.backend.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            UserRepository userRepository) {

        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuestionResponse createQuestion(
            String email,
            QuestionRequest request) {

        User user = getUserByEmail(email);

        Question question = new Question();

        question.setUser(user);
        question.setQuestionText(request.getQuestionText());
        question.setAnswer(request.getAnswer());
        question.setCategory(request.getCategory());
        question.setTopic(request.getTopic());
        question.setQuestionType(request.getQuestionType());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setTags(request.getTags());

        Question savedQuestion =
                questionRepository.save(question);

        return mapToResponse(savedQuestion);
    }

    @Override
    public PageResponse<QuestionResponse> getAllQuestions(
            String email,
            int page,
            int size,
            String category,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionType questionType) {

        User user = getUserByEmail(email);

        Pageable pageable =
                PageRequest.of(page, size);

        Specification<Question> specification =
                Specification
                        .where(
                                QuestionSpecification
                                        .belongsToUser(user.getId())
                        )
                        .and(
                                QuestionSpecification
                                        .hasCategory(category)
                        )
                        .and(
                                QuestionSpecification
                                        .hasTopic(topic)
                        )
                        .and(
                                QuestionSpecification
                                        .hasDifficulty(difficultyLevel)
                        )
                        .and(
                                QuestionSpecification
                                        .hasQuestionType(questionType)
                        );

        Page<Question> questionPage =
                questionRepository.findAll(
                        specification,
                        pageable
                );

        var content =
                questionPage.getContent()
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
    public QuestionResponse getQuestionById(
            String email,
            UUID id) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(id, user.getId());

        return mapToResponse(question);
    }

    @Override
    public QuestionResponse updateQuestion(
            String email,
            UUID id,
            QuestionRequest request) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(id, user.getId());

        question.setQuestionText(request.getQuestionText());
        question.setAnswer(request.getAnswer());
        question.setCategory(request.getCategory());
        question.setTopic(request.getTopic());
        question.setQuestionType(request.getQuestionType());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setTags(request.getTags());

        Question updatedQuestion =
                questionRepository.save(question);

        return mapToResponse(updatedQuestion);
    }

    @Override
    public void deleteQuestion(
            String email,
            UUID id) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(id, user.getId());

        questionRepository.delete(question);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found."
                        )
                );
    }

    private Question getOwnedQuestion(
            UUID questionId,
            UUID userId) {

        return questionRepository
                .findByIdAndUserId(
                        questionId,
                        userId
                )
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found."
                        )
                );
    }

    private QuestionResponse mapToResponse(
            Question question) {

        QuestionResponse response =
                new QuestionResponse();

        response.setId(question.getId());
        response.setQuestionText(
                question.getQuestionText()
        );
        response.setAnswer(question.getAnswer());
        response.setCategory(question.getCategory());
        response.setTopic(question.getTopic());
        response.setQuestionType(
                question.getQuestionType()
        );
        response.setDifficultyLevel(
                question.getDifficultyLevel()
        );
        response.setTags(question.getTags());
        response.setCreatedAt(
                question.getCreatedAt()
        );
        response.setUpdatedAt(
                question.getUpdatedAt()
        );

        return response;
    }
}