package com.prepnovis.backend.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public QuestionResponse createQuestion(
        String email,
        QuestionRequest request) {

    /*
     * Lock this user's row while allocating the next permanent
     * Saved Question number.
     *
     * This prevents two simultaneous requests from receiving
     * the same question number.
     */
    User user = userRepository
            .findByEmailForQuestionNumberUpdate(email)
            .orElseThrow(
                    () -> new UserNotFoundException(
                            "User not found."
                    )
            );

    Long nextQuestionNumber =
            user.getNextQuestionNumber();

    /*
     * Reserve the number immediately.
     *
     * Example:
     * current counter = 7
     * new question     = #7
     * counter becomes  = 8
     *
     * Deleting #7 later does NOT decrease this counter,
     * therefore #7 will never be reused.
     */
    user.setNextQuestionNumber(
            nextQuestionNumber + 1
    );

    Question question = new Question();

    question.setUser(user);
    question.setQuestionNumber(nextQuestionNumber);
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
    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> getAllQuestions(
            String email,
            int page,
            int size,
            String search,
            String category,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionType questionType) {

        User user = getUserByEmail(email);

        /*
         * Always keep Saved Questions in permanent question-number order.
         *
         * Example:
         * #1, #2, #4, #5
         *
         * If #3 was deleted, the remaining questions are NOT renumbered.
         */
        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "questionNumber"
                        )
                );

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

        /*
         * Search behavior:
         *
         * #25 -> exact permanent question #25
         * 25  -> exact permanent question #25
         *
         * kafka -> keyword search
         * spring boot -> keyword search
         */
        if (search != null && !search.isBlank()) {

            String normalizedSearch =
                    search.trim();

            String numericPart =
                    normalizedSearch.startsWith("#")
                            ? normalizedSearch.substring(1).trim()
                            : normalizedSearch;

            if (isPositiveWholeNumber(numericPart)) {

                try {
                    Long questionNumber =
                            Long.valueOf(numericPart);

                    specification =
                            specification.and(
                                    QuestionSpecification
                                            .hasQuestionNumber(
                                                    questionNumber
                                            )
                            );

                } catch (NumberFormatException exception) {

                    /*
                     * Extremely large numeric input that cannot fit in Long.
                     * Treat it as normal text rather than failing the API.
                     */
                    specification =
                            specification.and(
                                    QuestionSpecification
                                            .containsKeyword(
                                                    normalizedSearch
                                            )
                            );
                }

            } else {

                specification =
                        specification.and(
                                QuestionSpecification
                                        .containsKeyword(
                                                normalizedSearch
                                        )
                        );
            }
        }

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
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(
            String email,
            UUID id) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(
                        id,
                        user.getId()
                );

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(
            String email,
            UUID id,
            QuestionRequest request) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(
                        id,
                        user.getId()
                );

        /*
         * Do NOT modify questionNumber.
         * It is permanent once the question is created.
         */
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
    @Transactional
    public void deleteQuestion(
            String email,
            UUID id) {

        User user = getUserByEmail(email);

        Question question =
                getOwnedQuestion(
                        id,
                        user.getId()
                );

        /*
         * Deleting a question does NOT renumber
         * any of the remaining questions.
         */
        questionRepository.delete(question);
    }

    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
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
                .orElseThrow(
                        () -> new QuestionNotFoundException(
                                "Question not found."
                        )
                );
    }

    private boolean isPositiveWholeNumber(
            String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {

            if (!Character.isDigit(
                    value.charAt(i))) {

                return false;
            }
        }

        return true;
    }

    private QuestionResponse mapToResponse(
            Question question) {

        QuestionResponse response =
                new QuestionResponse();

        response.setId(question.getId());

        response.setQuestionNumber(
                question.getQuestionNumber()
        );

        response.setQuestionText(
                question.getQuestionText()
        );

        response.setAnswer(
                question.getAnswer()
        );

        response.setCategory(
                question.getCategory()
        );

        response.setTopic(
                question.getTopic()
        );

        response.setQuestionType(
                question.getQuestionType()
        );

        response.setDifficultyLevel(
                question.getDifficultyLevel()
        );

        response.setTags(
                question.getTags()
        );

        response.setCreatedAt(
                question.getCreatedAt()
        );

        response.setUpdatedAt(
                question.getUpdatedAt()
        );

        return response;
    }
}