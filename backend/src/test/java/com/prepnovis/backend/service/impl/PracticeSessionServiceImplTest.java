package com.prepnovis.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prepnovis.backend.dto.event.AnswerEvaluatedEvent;
import com.prepnovis.backend.dto.request.StartPracticeSessionRequest;
import com.prepnovis.backend.dto.request.SubmitPracticeAnswerRequest;
import com.prepnovis.backend.dto.response.AnswerEvaluationResult;
import com.prepnovis.backend.dto.response.GeneratedMockQuestion;
import com.prepnovis.backend.dto.response.PracticeSessionDetailResponse;
import com.prepnovis.backend.dto.response.PracticeSessionQuestionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResultResponse;
import com.prepnovis.backend.entity.PracticeSession;
import com.prepnovis.backend.entity.PracticeSessionQuestion;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.QuestionSource;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.PracticeSessionStatus;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.exception.InvalidPracticeSessionQuestionException;
import com.prepnovis.backend.exception.InvalidPracticeSessionStateException;
import com.prepnovis.backend.exception.PracticeSessionAccessDeniedException;
import com.prepnovis.backend.exception.PracticeSessionNotFoundException;
import com.prepnovis.backend.exception.PracticeSessionQuestionNotFoundException;
import com.prepnovis.backend.exception.UserNotFoundException;
import com.prepnovis.backend.repository.PracticeSessionQuestionRepository;
import com.prepnovis.backend.repository.PracticeSessionRepository;
import com.prepnovis.backend.repository.QuestionRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.AnswerEvaluationService;
import com.prepnovis.backend.service.AnswerEventPublisher;
import com.prepnovis.backend.service.MockQuestionGenerationService;

@ExtendWith(MockitoExtension.class)
class PracticeSessionServiceImplTest {

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private PracticeSessionQuestionRepository practiceSessionQuestionRepository;

    @Mock
    private AnswerEvaluationService answerEvaluationService;

    @Mock
    private MockQuestionGenerationService mockQuestionGenerationService;

    @Mock
    private AnswerEventPublisher answerEventPublisher;

    @InjectMocks
    private PracticeSessionServiceImpl practiceSessionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("prince@test.com");
        user.setFullName("Prince Kumar");
    }

    @Test
    void startSession_ShouldStartSavedQuestionSessionSuccessfully() {

        StartPracticeSessionRequest request = createStartRequest();
        request.setQuestionSource(QuestionSource.SAVED);
        request.setTotalQuestions(2);

        Question question1 = createQuestion("What is Spring Boot?");
        Question question2 = createQuestion("What is dependency injection?");

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.save(any(PracticeSession.class)))
                .thenAnswer(invocation -> {
                    PracticeSession session = invocation.getArgument(0);
                    session.setId(UUID.randomUUID());
                    return session;
                });

        when(questionRepository
                .findByCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
                        request.getCategory(),
                        request.getTopic(),
                        request.getDifficultyLevel(),
                        request.getQuestionType()))
                .thenReturn(List.of(question1, question2));

        PracticeSessionResponse response =
                practiceSessionService.startSession(
                        "prince@test.com",
                        request
                );

        assertEquals(2, response.getAssignedQuestions());
        assertEquals(2, response.getTotalQuestions());
        assertEquals(
                PracticeSessionStatus.IN_PROGRESS,
                response.getStatus()
        );

        verify(practiceSessionQuestionRepository,
                org.mockito.Mockito.times(2))
                .save(any(PracticeSessionQuestion.class));
    }

    @Test
    void startSession_ShouldDefaultToSavedQuestions_WhenSourceIsNull() {

        StartPracticeSessionRequest request = createStartRequest();
        request.setQuestionSource(null);
        request.setTotalQuestions(1);

        Question question = createQuestion("What is Spring Boot?");

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.save(any(PracticeSession.class)))
                .thenAnswer(invocation -> {
                    PracticeSession session = invocation.getArgument(0);
                    session.setId(UUID.randomUUID());
                    return session;
                });

        when(questionRepository
                .findByCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
                        request.getCategory(),
                        request.getTopic(),
                        request.getDifficultyLevel(),
                        request.getQuestionType()))
                .thenReturn(List.of(question));

        PracticeSessionResponse response =
                practiceSessionService.startSession(
                        "prince@test.com",
                        request
                );

        assertEquals(1, response.getAssignedQuestions());

        ArgumentCaptor<PracticeSession> captor =
                ArgumentCaptor.forClass(PracticeSession.class);

        verify(practiceSessionRepository).save(captor.capture());

        assertEquals(
                QuestionSource.SAVED,
                captor.getValue().getQuestionSource()
        );

        verify(mockQuestionGenerationService, never())
                .generateQuestions(
                        any(),
                        any(),
                        any(),
                        any(),
                        org.mockito.ArgumentMatchers.anyInt()
                );
    }

    @Test
    void startSession_ShouldStartPrepNovisMockSessionSuccessfully() {

        StartPracticeSessionRequest request = createStartRequest();
        request.setQuestionSource(QuestionSource.PREPNOVIS_MOCK);
        request.setTotalQuestions(2);

        GeneratedMockQuestion mock1 = new GeneratedMockQuestion();
        mock1.setQuestionText("Explain dependency injection.");
        mock1.setReferenceAnswer("Dependencies are provided externally.");

        GeneratedMockQuestion mock2 = new GeneratedMockQuestion();
        mock2.setQuestionText("Explain Spring Boot.");
        mock2.setReferenceAnswer("Spring Boot simplifies Spring applications.");

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.save(any(PracticeSession.class)))
                .thenAnswer(invocation -> {
                    PracticeSession session = invocation.getArgument(0);
                    session.setId(UUID.randomUUID());
                    return session;
                });

        when(mockQuestionGenerationService.generateQuestions(
                request.getCategory(),
                request.getTopic(),
                request.getDifficultyLevel(),
                request.getQuestionType(),
                request.getTotalQuestions()))
                .thenReturn(List.of(mock1, mock2));

        PracticeSessionResponse response =
                practiceSessionService.startSession(
                        "prince@test.com",
                        request
                );

        assertEquals(2, response.getAssignedQuestions());
        assertEquals(
                PracticeSessionStatus.IN_PROGRESS,
                response.getStatus()
        );

        verify(mockQuestionGenerationService)
                .generateQuestions(
                        request.getCategory(),
                        request.getTopic(),
                        request.getDifficultyLevel(),
                        request.getQuestionType(),
                        2
                );

        verify(practiceSessionQuestionRepository,
                org.mockito.Mockito.times(2))
                .save(any(PracticeSessionQuestion.class));

        verify(questionRepository, never())
                .findByCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void startSession_ShouldThrowException_WhenUserDoesNotExist() {

        StartPracticeSessionRequest request = createStartRequest();

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> practiceSessionService.startSession(
                                "missing@test.com",
                                request
                        )
                );

        assertEquals("User not found.", exception.getMessage());

        verify(practiceSessionRepository, never())
                .save(any(PracticeSession.class));
    }

    @Test
    void getSessionDetails_ShouldReturnSavedQuestionDetails() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        Question question = createQuestion("What is Spring Boot?");

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setId(UUID.randomUUID());
        sessionQuestion.setPracticeSession(session);
        sessionQuestion.setQuestion(question);
        sessionQuestion.setAnswered(false);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionId(sessionId))
                .thenReturn(List.of(sessionQuestion));

        PracticeSessionDetailResponse response =
                practiceSessionService.getSessionDetails(
                        "prince@test.com",
                        sessionId
                );

        assertEquals(sessionId, response.getId());
        assertEquals(1, response.getQuestions().size());

        PracticeSessionQuestionResponse questionResponse =
                response.getQuestions().get(0);

        assertEquals(question.getId(), questionResponse.getQuestionId());
        assertEquals(
                "What is Spring Boot?",
                questionResponse.getQuestionText()
        );
        assertFalse(questionResponse.getAnswered());
    }

    @Test
    void getSessionDetails_ShouldThrowException_WhenSessionDoesNotExist() {

        UUID sessionId = UUID.randomUUID();

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        PracticeSessionNotFoundException exception =
                assertThrows(
                        PracticeSessionNotFoundException.class,
                        () -> practiceSessionService.getSessionDetails(
                                "prince@test.com",
                                sessionId
                        )
                );

        assertEquals(
                "Practice session not found.",
                exception.getMessage()
        );
    }

    @Test
    void getSessionDetails_ShouldThrowException_WhenUserDoesNotOwnSession() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        PracticeSessionAccessDeniedException exception =
                assertThrows(
                        PracticeSessionAccessDeniedException.class,
                        () -> practiceSessionService.getSessionDetails(
                                "another@test.com",
                                sessionId
                        )
                );

        assertEquals(
                "You are not allowed to access this session.",
                exception.getMessage()
        );

        verify(practiceSessionQuestionRepository, never())
                .findByPracticeSessionId(sessionId);
    }

    @Test
    void submitAnswer_ShouldEvaluateSaveAndPublishEventSuccessfully() {

        UUID sessionId = UUID.randomUUID();
        UUID sessionQuestionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        session.setQuestionSource(QuestionSource.SAVED);

        Question question = createQuestion("What is Spring Boot?");
        question.setAnswer("Spring Boot simplifies Spring application development.");

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setId(sessionQuestionId);
        sessionQuestion.setPracticeSession(session);
        sessionQuestion.setQuestion(question);
        sessionQuestion.setAnswered(false);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        request.setAnswer(
                "Spring Boot helps us create Spring applications quickly."
        );

        AnswerEvaluationResult evaluation =
                new AnswerEvaluationResult();

        evaluation.setScore(8.5);
        evaluation.setFeedback("Good answer.");
        evaluation.setStrengths(List.of("Clear explanation"));
        evaluation.setImprovements(List.of("Add more detail"));

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository.findById(sessionQuestionId))
                .thenReturn(Optional.of(sessionQuestion));

        when(answerEvaluationService.evaluateAnswer(
                question.getQuestionText(),
                question.getAnswer(),
                request.getAnswer()))
                .thenReturn(evaluation);

        when(practiceSessionQuestionRepository
                .save(any(PracticeSessionQuestion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PracticeSessionQuestionResponse response =
                practiceSessionService.submitAnswer(
                        "prince@test.com",
                        sessionId,
                        sessionQuestionId,
                        request
                );

        assertTrue(response.getAnswered());
        assertEquals(8.5, response.getScore());
        assertEquals("Good answer.", response.getFeedback());
        assertEquals(
                "Clear explanation",
                response.getStrengths()
        );
        assertEquals(
                "Add more detail",
                response.getImprovements()
        );

        verify(answerEvaluationService)
                .evaluateAnswer(
                        question.getQuestionText(),
                        question.getAnswer(),
                        request.getAnswer()
                );

        verify(practiceSessionQuestionRepository)
                .save(sessionQuestion);

        verify(answerEventPublisher)
                .publishAnswerEvaluatedEvent(
                        any(AnswerEvaluatedEvent.class)
                );
    }

    @Test
    void submitAnswer_ShouldThrowException_WhenSessionIsCompleted() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.COMPLETED);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        request.setAnswer("Some answer");

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        InvalidPracticeSessionStateException exception =
                assertThrows(
                        InvalidPracticeSessionStateException.class,
                        () -> practiceSessionService.submitAnswer(
                                "prince@test.com",
                                sessionId,
                                UUID.randomUUID(),
                                request
                        )
                );

        assertEquals(
                "Practice session is already completed.",
                exception.getMessage()
        );

        verify(answerEvaluationService, never())
                .evaluateAnswer(any(), any(), any());
    }

    @Test
    void submitAnswer_ShouldThrowException_WhenSessionQuestionDoesNotExist() {

        UUID sessionId = UUID.randomUUID();
        UUID sessionQuestionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        request.setAnswer("Some answer");

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository.findById(sessionQuestionId))
                .thenReturn(Optional.empty());

        PracticeSessionQuestionNotFoundException exception =
                assertThrows(
                        PracticeSessionQuestionNotFoundException.class,
                        () -> practiceSessionService.submitAnswer(
                                "prince@test.com",
                                sessionId,
                                sessionQuestionId,
                                request
                        )
                );

        assertEquals(
                "Practice session question not found.",
                exception.getMessage()
        );
    }

    @Test
    void submitAnswer_ShouldThrowException_WhenQuestionBelongsToDifferentSession() {

        UUID sessionId = UUID.randomUUID();
        UUID anotherSessionId = UUID.randomUUID();
        UUID sessionQuestionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        PracticeSession anotherSession =
                createSession(
                        anotherSessionId,
                        PracticeSessionStatus.IN_PROGRESS
                );

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setId(sessionQuestionId);
        sessionQuestion.setPracticeSession(anotherSession);
        sessionQuestion.setAnswered(false);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        request.setAnswer("Some answer");

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository.findById(sessionQuestionId))
                .thenReturn(Optional.of(sessionQuestion));

        InvalidPracticeSessionQuestionException exception =
                assertThrows(
                        InvalidPracticeSessionQuestionException.class,
                        () -> practiceSessionService.submitAnswer(
                                "prince@test.com",
                                sessionId,
                                sessionQuestionId,
                                request
                        )
                );

        assertEquals(
                "Question does not belong to this practice session.",
                exception.getMessage()
        );

        verify(answerEvaluationService, never())
                .evaluateAnswer(any(), any(), any());
    }

    @Test
    void submitAnswer_ShouldThrowException_WhenQuestionAlreadyAnswered() {

        UUID sessionId = UUID.randomUUID();
        UUID sessionQuestionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setId(sessionQuestionId);
        sessionQuestion.setPracticeSession(session);
        sessionQuestion.setAnswered(true);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        request.setAnswer("Another answer");

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository.findById(sessionQuestionId))
                .thenReturn(Optional.of(sessionQuestion));

        InvalidPracticeSessionStateException exception =
                assertThrows(
                        InvalidPracticeSessionStateException.class,
                        () -> practiceSessionService.submitAnswer(
                                "prince@test.com",
                                sessionId,
                                sessionQuestionId,
                                request
                        )
                );

        assertEquals(
                "This question has already been answered.",
                exception.getMessage()
        );

        verify(answerEvaluationService, never())
                .evaluateAnswer(any(), any(), any());
    }

    @Test
    void completeSession_ShouldCalculateResultAndCompleteSession() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        session.setTotalQuestions(3);

        PracticeSessionQuestion question1 =
                createSessionQuestion(session, true, 8.0);

        PracticeSessionQuestion question2 =
                createSessionQuestion(session, true, 6.0);

        PracticeSessionQuestion question3 =
                createSessionQuestion(session, false, null);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionId(sessionId))
                .thenReturn(List.of(
                        question1,
                        question2,
                        question3
                ));

        when(practiceSessionRepository.save(session))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PracticeSessionResultResponse response =
                practiceSessionService.completeSession(
                        "prince@test.com",
                        sessionId
                );

        assertEquals(3, response.getAssignedQuestions());
        assertEquals(2, response.getAnsweredQuestions());
        assertEquals(1, response.getUnansweredQuestions());
        assertEquals(7.0, response.getAverageScore());
        assertEquals(
                PracticeSessionStatus.COMPLETED,
                response.getStatus()
        );

        verify(practiceSessionRepository).save(session);
    }

    @Test
    void completeSession_ShouldThrowException_WhenAlreadyCompleted() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.COMPLETED);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        InvalidPracticeSessionStateException exception =
                assertThrows(
                        InvalidPracticeSessionStateException.class,
                        () -> practiceSessionService.completeSession(
                                "prince@test.com",
                                sessionId
                        )
                );

        assertEquals(
                "Practice session is already completed.",
                exception.getMessage()
        );

        verify(practiceSessionRepository, never())
                .save(any(PracticeSession.class));
    }

    @Test
    void getSessionResult_ShouldReturnCompletedSessionResult() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.COMPLETED);

        session.setTotalQuestions(2);
        session.setCompletedAt(LocalDateTime.now());

        PracticeSessionQuestion question1 =
                createSessionQuestion(session, true, 9.0);

        PracticeSessionQuestion question2 =
                createSessionQuestion(session, true, 7.0);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionId(sessionId))
                .thenReturn(List.of(question1, question2));

        PracticeSessionResultResponse response =
                practiceSessionService.getSessionResult(
                        "prince@test.com",
                        sessionId
                );

        assertEquals(sessionId, response.getSessionId());
        assertEquals(2, response.getAssignedQuestions());
        assertEquals(2, response.getAnsweredQuestions());
        assertEquals(0, response.getUnansweredQuestions());
        assertEquals(8.0, response.getAverageScore());
        assertEquals(
                PracticeSessionStatus.COMPLETED,
                response.getStatus()
        );
    }

    @Test
    void getSessionResult_ShouldThrowException_WhenSessionNotCompleted() {

        UUID sessionId = UUID.randomUUID();

        PracticeSession session =
                createSession(sessionId, PracticeSessionStatus.IN_PROGRESS);

        when(practiceSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        InvalidPracticeSessionStateException exception =
                assertThrows(
                        InvalidPracticeSessionStateException.class,
                        () -> practiceSessionService.getSessionResult(
                                "prince@test.com",
                                sessionId
                        )
                );

        assertEquals(
                "Practice session is not completed yet.",
                exception.getMessage()
        );

        verify(practiceSessionQuestionRepository, never())
                .findByPracticeSessionId(sessionId);
    }

    private StartPracticeSessionRequest createStartRequest() {

        StartPracticeSessionRequest request =
                new StartPracticeSessionRequest();

        request.setCategory("Java");
        request.setTopic("Spring Boot");
        request.setDifficultyLevel(DifficultyLevel.MEDIUM);
        request.setQuestionType(QuestionType.TECHNICAL);
        request.setTotalQuestions(2);

        return request;
    }

    private Question createQuestion(String questionText) {

        Question question = new Question();

        question.setId(UUID.randomUUID());
        question.setQuestionText(questionText);
        question.setAnswer("Reference answer");
        question.setCategory("Java");
        question.setTopic("Spring Boot");
        question.setDifficultyLevel(DifficultyLevel.MEDIUM);
        question.setQuestionType(QuestionType.TECHNICAL);

        return question;
    }

    private PracticeSession createSession(
            UUID sessionId,
            PracticeSessionStatus status) {

        PracticeSession session =
                new PracticeSession();

        session.setId(sessionId);
        session.setUser(user);
        session.setCategory("Java");
        session.setTopic("Spring Boot");
        session.setDifficultyLevel(DifficultyLevel.MEDIUM);
        session.setQuestionType(QuestionType.TECHNICAL);
        session.setTotalQuestions(2);
        session.setQuestionSource(QuestionSource.SAVED);
        session.setStatus(status);

        return session;
    }

    private PracticeSessionQuestion createSessionQuestion(
            PracticeSession session,
            boolean answered,
            Double score) {

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setId(UUID.randomUUID());
        sessionQuestion.setPracticeSession(session);
        sessionQuestion.setAnswered(answered);
        sessionQuestion.setScore(score);

        return sessionQuestion;
    }
}