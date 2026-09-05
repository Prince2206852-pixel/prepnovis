package com.prepnovis.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prepnovis.backend.dto.response.AnalyticsDashboardResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResultResponse;
import com.prepnovis.backend.entity.PracticeSession;
import com.prepnovis.backend.entity.PracticeSessionQuestion;
import com.prepnovis.backend.entity.QuestionSource;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.PracticeSessionStatus;
import com.prepnovis.backend.repository.PracticeSessionQuestionRepository;
import com.prepnovis.backend.repository.PracticeSessionRepository;
import com.prepnovis.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private PracticeSessionQuestionRepository practiceSessionQuestionRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("prince@test.com");
        user.setFullName("Prince Kumar");
    }

    @Test
    void getDashboard_ShouldCalculateAnalyticsSuccessfully() {

        PracticeSession savedSession =
                createSession(
                        QuestionSource.SAVED,
                        PracticeSessionStatus.COMPLETED,
                        2
                );

        PracticeSession mockSession =
                createSession(
                        QuestionSource.PREPNOVIS_MOCK,
                        PracticeSessionStatus.COMPLETED,
                        2
                );

        PracticeSession inProgressSession =
                createSession(
                        QuestionSource.SAVED,
                        PracticeSessionStatus.IN_PROGRESS,
                        1
                );

        PracticeSessionQuestion savedQuestion1 =
                createQuestion(savedSession, true, 8.0);

        PracticeSessionQuestion savedQuestion2 =
                createQuestion(savedSession, true, 6.0);

        PracticeSessionQuestion mockQuestion1 =
                createQuestion(mockSession, true, 9.0);

        PracticeSessionQuestion mockQuestion2 =
                createQuestion(mockSession, true, 7.0);

        PracticeSessionQuestion unansweredQuestion =
                createQuestion(inProgressSession, false, null);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(
                        savedSession,
                        mockSession,
                        inProgressSession
                ));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of(
                        savedQuestion1,
                        savedQuestion2,
                        mockQuestion1,
                        mockQuestion2,
                        unansweredQuestion
                ));

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of());

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(3, response.getTotalSessions());
        assertEquals(2, response.getCompletedSessions());

        assertEquals(4, response.getTotalQuestionsAnswered());

        assertEquals(
                2,
                response.getSavedQuestionsAnswered()
        );

        assertEquals(
                2,
                response.getPrepNovisMockQuestionsAnswered()
        );

        assertEquals(7.5, response.getAverageScore());

        assertEquals(
                7.0,
                response.getSavedQuestionsAverageScore()
        );

        assertEquals(
                8.0,
                response.getPrepNovisMockAverageScore()
        );

        assertEquals(9.0, response.getHighestScore());

        assertEquals(0, response.getRecentSessions().size());
    }

    @Test
    void getDashboard_ShouldIgnoreNullScoresInAverageAndHighestScore() {

        PracticeSession session =
                createSession(
                        QuestionSource.SAVED,
                        PracticeSessionStatus.COMPLETED,
                        3
                );

        PracticeSessionQuestion question1 =
                createQuestion(session, true, 8.0);

        PracticeSessionQuestion question2 =
                createQuestion(session, true, null);

        PracticeSessionQuestion question3 =
                createQuestion(session, true, 6.0);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of(
                        question1,
                        question2,
                        question3
                ));

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of());

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(3, response.getTotalQuestionsAnswered());

        assertEquals(7.0, response.getAverageScore());

        assertEquals(7.0, response.getSavedQuestionsAverageScore());

        assertEquals(8.0, response.getHighestScore());
    }

    @Test
    void getDashboard_ShouldRoundAverageScoresToTwoDecimalPlaces() {

        PracticeSession savedSession =
                createSession(
                        QuestionSource.SAVED,
                        PracticeSessionStatus.COMPLETED,
                        3
                );

        PracticeSessionQuestion question1 =
                createQuestion(savedSession, true, 8.0);

        PracticeSessionQuestion question2 =
                createQuestion(savedSession, true, 7.0);

        PracticeSessionQuestion question3 =
                createQuestion(savedSession, true, 7.0);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(savedSession));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of(
                        question1,
                        question2,
                        question3
                ));

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of());

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(7.33, response.getAverageScore());

        assertEquals(
                7.33,
                response.getSavedQuestionsAverageScore()
        );
    }

    @Test
    void getDashboard_ShouldReturnZeros_WhenUserHasNoPracticeData() {

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of());

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of());

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of());

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(0, response.getTotalSessions());
        assertEquals(0, response.getCompletedSessions());
        assertEquals(0, response.getTotalQuestionsAnswered());

        assertEquals(0, response.getSavedQuestionsAnswered());

        assertEquals(
                0,
                response.getPrepNovisMockQuestionsAnswered()
        );

        assertEquals(0.0, response.getAverageScore());

        assertEquals(
                0.0,
                response.getSavedQuestionsAverageScore()
        );

        assertEquals(
                0.0,
                response.getPrepNovisMockAverageScore()
        );

        assertEquals(0.0, response.getHighestScore());

        assertEquals(0, response.getRecentSessions().size());
    }

    @Test
    void getDashboard_ShouldBuildRecentSessionResultsSuccessfully() {

        PracticeSession session =
                createSession(
                        QuestionSource.PREPNOVIS_MOCK,
                        PracticeSessionStatus.COMPLETED,
                        3
                );

        LocalDateTime completedAt = LocalDateTime.now();
        session.setCompletedAt(completedAt);

        PracticeSessionQuestion question1 =
                createQuestion(session, true, 9.0);

        PracticeSessionQuestion question2 =
                createQuestion(session, true, 8.0);

        PracticeSessionQuestion question3 =
                createQuestion(session, false, null);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of(
                        question1,
                        question2,
                        question3
                ));

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of(session));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionId(session.getId()))
                .thenReturn(List.of(
                        question1,
                        question2,
                        question3
                ));

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(1, response.getRecentSessions().size());

        PracticeSessionResultResponse recent =
                response.getRecentSessions().get(0);

        assertEquals(session.getId(), recent.getSessionId());
        assertEquals(3, recent.getTotalQuestions());
        assertEquals(3, recent.getAssignedQuestions());
        assertEquals(2, recent.getAnsweredQuestions());
        assertEquals(1, recent.getUnansweredQuestions());
        assertEquals(8.5, recent.getAverageScore());

        assertEquals(
                PracticeSessionStatus.COMPLETED,
                recent.getStatus()
        );

        assertEquals(
                completedAt,
                recent.getCompletedAt()
        );
    }

    @Test
    void getDashboard_ShouldKeepSavedAndMockAnalyticsSeparate() {

        PracticeSession savedSession =
                createSession(
                        QuestionSource.SAVED,
                        PracticeSessionStatus.COMPLETED,
                        1
                );

        PracticeSession mockSession =
                createSession(
                        QuestionSource.PREPNOVIS_MOCK,
                        PracticeSessionStatus.COMPLETED,
                        2
                );

        PracticeSessionQuestion savedQuestion =
                createQuestion(savedSession, true, 5.0);

        PracticeSessionQuestion mockQuestion1 =
                createQuestion(mockSession, true, 9.0);

        PracticeSessionQuestion mockQuestion2 =
                createQuestion(mockSession, true, 10.0);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(practiceSessionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(
                        savedSession,
                        mockSession
                ));

        when(practiceSessionQuestionRepository
                .findByPracticeSessionUserId(user.getId()))
                .thenReturn(List.of(
                        savedQuestion,
                        mockQuestion1,
                        mockQuestion2
                ));

        when(practiceSessionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of());

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard("prince@test.com");

        assertEquals(1, response.getSavedQuestionsAnswered());

        assertEquals(
                2,
                response.getPrepNovisMockQuestionsAnswered()
        );

        assertEquals(
                5.0,
                response.getSavedQuestionsAverageScore()
        );

        assertEquals(
                9.5,
                response.getPrepNovisMockAverageScore()
        );

        assertEquals(8.0, response.getAverageScore());

        assertEquals(10.0, response.getHighestScore());
    }

    @Test
    void getDashboard_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> analyticsService.getDashboard(
                                "missing@test.com"
                        )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(practiceSessionRepository, never())
                .findByUserId(user.getId());

        verify(practiceSessionQuestionRepository, never())
                .findByPracticeSessionUserId(user.getId());
    }

    private PracticeSession createSession(
            QuestionSource source,
            PracticeSessionStatus status,
            int totalQuestions) {

        PracticeSession session =
                new PracticeSession();

        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setQuestionSource(source);
        session.setStatus(status);
        session.setTotalQuestions(totalQuestions);

        return session;
    }

    private PracticeSessionQuestion createQuestion(
            PracticeSession session,
            boolean answered,
            Double score) {

        PracticeSessionQuestion question =
                new PracticeSessionQuestion();

        question.setId(UUID.randomUUID());
        question.setPracticeSession(session);
        question.setAnswered(answered);
        question.setScore(score);

        return question;
    }
}