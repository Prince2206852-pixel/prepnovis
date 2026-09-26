package com.prepnovis.backend.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.exception.QuestionNotFoundException;
import com.prepnovis.backend.repository.QuestionRepository;
import com.prepnovis.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    private static final String EMAIL = "prince@test.com";

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(EMAIL);
        user.setFullName("Prince Kumar");
        user.setNextQuestionNumber(1L);
    }

    @Test
    void createQuestion_ShouldCreateQuestionSuccessfully() {

        QuestionRequest request = createQuestionRequest();

        when(userRepository.findByEmailForQuestionNumberUpdate(EMAIL))
                .thenReturn(Optional.of(user));

        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation -> {

                    Question question = invocation.getArgument(0);
                    question.setId(UUID.randomUUID());

                    return question;
                });

        QuestionResponse response =
                questionService.createQuestion(
                        EMAIL,
                        request
                );

        assertEquals(
                "What is dependency injection?",
                response.getQuestionText()
        );

        assertEquals(
                "Dependency injection provides required dependencies from outside.",
                response.getAnswer()
        );

        assertEquals("Java", response.getCategory());
        assertEquals("Spring Boot", response.getTopic());

        assertEquals(
                QuestionType.TECHNICAL,
                response.getQuestionType()
        );

        assertEquals(
                DifficultyLevel.MEDIUM,
                response.getDifficultyLevel()
        );

        assertEquals("spring,di", response.getTags());

        // Verify permanent per-user question numbering.
        assertEquals(1L, response.getQuestionNumber());

        // Counter should move forward after allocating #1.
        assertEquals(2L, user.getNextQuestionNumber());

        verify(userRepository)
                .findByEmailForQuestionNumberUpdate(EMAIL);

        verify(questionRepository)
                .save(any(Question.class));
    }

    @Test
    void getQuestionById_ShouldReturnQuestionSuccessfully() {

        UUID questionId = UUID.randomUUID();

        Question question =
                createQuestion(questionId);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(questionRepository.findByIdAndUserId(
                questionId,
                user.getId()))
                .thenReturn(Optional.of(question));

        QuestionResponse response =
                questionService.getQuestionById(
                        EMAIL,
                        questionId
                );

        assertEquals(questionId, response.getId());

        assertEquals(
                "What is dependency injection?",
                response.getQuestionText()
        );

        assertEquals("Java", response.getCategory());
        assertEquals("Spring Boot", response.getTopic());

        assertEquals(
                QuestionType.TECHNICAL,
                response.getQuestionType()
        );

        assertEquals(
                DifficultyLevel.MEDIUM,
                response.getDifficultyLevel()
        );

        verify(userRepository)
                .findByEmail(EMAIL);

        verify(questionRepository)
                .findByIdAndUserId(
                        questionId,
                        user.getId()
                );
    }

    @Test
    void getQuestionById_ShouldThrowException_WhenQuestionDoesNotExist() {

        UUID questionId = UUID.randomUUID();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(questionRepository.findByIdAndUserId(
                questionId,
                user.getId()))
                .thenReturn(Optional.empty());

        QuestionNotFoundException exception =
                assertThrows(
                        QuestionNotFoundException.class,
                        () -> questionService.getQuestionById(
                                EMAIL,
                                questionId
                        )
                );

        assertEquals(
                "Question not found.",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(EMAIL);

        verify(questionRepository)
                .findByIdAndUserId(
                        questionId,
                        user.getId()
                );
    }

    @Test
    void updateQuestion_ShouldUpdateQuestionSuccessfully() {

        UUID questionId = UUID.randomUUID();

        Question existingQuestion = new Question();
        existingQuestion.setId(questionId);
        existingQuestion.setUser(user);
        existingQuestion.setQuestionNumber(1L);
        existingQuestion.setQuestionText("Old question");
        existingQuestion.setAnswer("Old answer");
        existingQuestion.setCategory("Java");
        existingQuestion.setTopic("Core Java");
        existingQuestion.setQuestionType(
                QuestionType.TECHNICAL
        );
        existingQuestion.setDifficultyLevel(
                DifficultyLevel.EASY
        );
        existingQuestion.setTags("java");

        QuestionRequest request =
                createQuestionRequest();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(questionRepository.findByIdAndUserId(
                questionId,
                user.getId()))
                .thenReturn(Optional.of(existingQuestion));

        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        QuestionResponse response =
                questionService.updateQuestion(
                        EMAIL,
                        questionId,
                        request
                );

        assertEquals(questionId, response.getId());

        assertEquals(
                "What is dependency injection?",
                response.getQuestionText()
        );

        assertEquals(
                "Dependency injection provides required dependencies from outside.",
                response.getAnswer()
        );

        assertEquals(
                "Spring Boot",
                response.getTopic()
        );

        assertEquals(
                DifficultyLevel.MEDIUM,
                response.getDifficultyLevel()
        );

        assertEquals(
                "spring,di",
                response.getTags()
        );

        // Editing must never change the permanent question number.
        assertEquals(1L, response.getQuestionNumber());

        verify(userRepository)
                .findByEmail(EMAIL);

        verify(questionRepository)
                .findByIdAndUserId(
                        questionId,
                        user.getId()
                );

        verify(questionRepository)
                .save(existingQuestion);
    }

    @Test
    void deleteQuestion_ShouldDeleteQuestionSuccessfully() {

        UUID questionId = UUID.randomUUID();

        Question question =
                createQuestion(questionId);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(questionRepository.findByIdAndUserId(
                questionId,
                user.getId()))
                .thenReturn(Optional.of(question));

        questionService.deleteQuestion(
                EMAIL,
                questionId
        );

        verify(userRepository)
                .findByEmail(EMAIL);

        verify(questionRepository)
                .findByIdAndUserId(
                        questionId,
                        user.getId()
                );

        verify(questionRepository)
                .delete(question);
    }

    private QuestionRequest createQuestionRequest() {

        QuestionRequest request =
                new QuestionRequest();

        request.setQuestionText(
                "What is dependency injection?"
        );

        request.setAnswer(
                "Dependency injection provides required dependencies from outside."
        );

        request.setCategory("Java");
        request.setTopic("Spring Boot");

        request.setQuestionType(
                QuestionType.TECHNICAL
        );

        request.setDifficultyLevel(
                DifficultyLevel.MEDIUM
        );

        request.setTags("spring,di");

        return request;
    }

    private Question createQuestion(UUID questionId) {

        Question question = new Question();

        question.setId(questionId);
        question.setUser(user);
        question.setQuestionNumber(1L);

        question.setQuestionText(
                "What is dependency injection?"
        );

        question.setAnswer(
                "Dependency injection provides dependencies from outside."
        );

        question.setCategory("Java");
        question.setTopic("Spring Boot");

        question.setQuestionType(
                QuestionType.TECHNICAL
        );

        question.setDifficultyLevel(
                DifficultyLevel.MEDIUM
        );

        question.setTags("spring,di");

        return question;
    }
}