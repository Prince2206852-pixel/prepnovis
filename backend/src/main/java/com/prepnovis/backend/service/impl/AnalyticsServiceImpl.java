package com.prepnovis.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
import com.prepnovis.backend.service.AnalyticsService;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final PracticeSessionQuestionRepository practiceSessionQuestionRepository;

    public AnalyticsServiceImpl(
            UserRepository userRepository,
            PracticeSessionRepository practiceSessionRepository,
            PracticeSessionQuestionRepository practiceSessionQuestionRepository) {

        this.userRepository = userRepository;
        this.practiceSessionRepository = practiceSessionRepository;
        this.practiceSessionQuestionRepository = practiceSessionQuestionRepository;
    }

    @Override
    @Cacheable(value = "analyticsDashboard", key = "#email")
    public AnalyticsDashboardResponse getDashboard(String email) {

        // Step 1: Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Get all practice sessions of this user
        List<PracticeSession> sessions =
                practiceSessionRepository.findByUserId(user.getId());

        // Step 3: Get all practice questions of this user
        List<PracticeSessionQuestion> sessionQuestions =
                practiceSessionQuestionRepository
                        .findByPracticeSessionUserId(user.getId());

        // Step 4: Calculate total sessions
        int totalSessions = sessions.size();

        // Step 5: Calculate completed sessions
        int completedSessions = (int) sessions.stream()
                .filter(session ->
                        session.getStatus() == PracticeSessionStatus.COMPLETED)
                .count();

        // Step 6: Get answered questions
        List<PracticeSessionQuestion> answeredQuestions =
                sessionQuestions.stream()
                        .filter(question ->
                                Boolean.TRUE.equals(question.getAnswered()))
                        .toList();

        int totalQuestionsAnswered = answeredQuestions.size();

        List<PracticeSessionQuestion> savedAnsweredQuestions =
        answeredQuestions.stream()
                .filter(question ->
                        question.getPracticeSession().getQuestionSource()
                                == QuestionSource.SAVED)
                .toList();

        List<PracticeSessionQuestion> prepNovisMockAnsweredQuestions =
                answeredQuestions.stream()
                .filter(question ->
                        question.getPracticeSession().getQuestionSource()
                                == QuestionSource.PREPNOVIS_MOCK)
                .toList();

        int savedQuestionsAnswered =
        savedAnsweredQuestions.size();

        int prepNovisMockQuestionsAnswered =
        prepNovisMockAnsweredQuestions.size();

        // Step 7: Calculate average score
        double averageScore = answeredQuestions.stream()
                .filter(question -> question.getScore() != null)
                .mapToDouble(PracticeSessionQuestion::getScore)
                .average()
                .orElse(0.0);

        double savedQuestionsAverageScore =
        savedAnsweredQuestions.stream()
                .filter(question -> question.getScore() != null)
                .mapToDouble(PracticeSessionQuestion::getScore)
                .average()
                .orElse(0.0);

        double prepNovisMockAverageScore =
        prepNovisMockAnsweredQuestions.stream()
                .filter(question -> question.getScore() != null)
                .mapToDouble(PracticeSessionQuestion::getScore)
                .average()
                .orElse(0.0);

        averageScore =
        Math.round(averageScore * 100.0) / 100.0;

        savedQuestionsAverageScore =
        Math.round(savedQuestionsAverageScore * 100.0) / 100.0;

        prepNovisMockAverageScore =
        Math.round(prepNovisMockAverageScore * 100.0) / 100.0;        

        // Step 8: Calculate highest score
        double highestScore = answeredQuestions.stream()
                .filter(question -> question.getScore() != null)
                .mapToDouble(PracticeSessionQuestion::getScore)
                .max()
                .orElse(0.0);

        // Step 9: Get 5 most recent sessions
        List<PracticeSession> recentPracticeSessions =
                practiceSessionRepository
                        .findTop5ByUserIdOrderByCreatedAtDesc(user.getId());

        List<PracticeSessionResultResponse> recentSessions =
                new ArrayList<>();

        for (PracticeSession session : recentPracticeSessions) {

            List<PracticeSessionQuestion> questions =
                    practiceSessionQuestionRepository
                            .findByPracticeSessionId(session.getId());

            int assignedQuestions = questions.size();

            int answeredCount = (int) questions.stream()
                    .filter(question ->
                            Boolean.TRUE.equals(question.getAnswered()))
                    .count();

            int unansweredCount =
                    assignedQuestions - answeredCount;

            double sessionAverageScore = questions.stream()
                    .filter(question -> question.getScore() != null)
                    .mapToDouble(PracticeSessionQuestion::getScore)
                    .average()
                    .orElse(0.0);

            sessionAverageScore =
            Math.round(sessionAverageScore * 100.0) / 100.0;        

            PracticeSessionResultResponse response =
                    new PracticeSessionResultResponse();

            response.setSessionId(session.getId());
            response.setTotalQuestions(session.getTotalQuestions());
            response.setAssignedQuestions(assignedQuestions);
            response.setAnsweredQuestions(answeredCount);
            response.setUnansweredQuestions(unansweredCount);
            response.setAverageScore(sessionAverageScore);
            response.setStatus(session.getStatus());
            response.setCompletedAt(session.getCompletedAt());

            recentSessions.add(response);
        }

        // Step 10: Prepare dashboard response
        AnalyticsDashboardResponse dashboard =
                new AnalyticsDashboardResponse();

        dashboard.setTotalSessions(totalSessions);
        dashboard.setCompletedSessions(completedSessions);

        dashboard.setTotalQuestionsAnswered(totalQuestionsAnswered);
        dashboard.setSavedQuestionsAnswered(savedQuestionsAnswered);
        dashboard.setPrepNovisMockQuestionsAnswered(
        prepNovisMockQuestionsAnswered);

        dashboard.setAverageScore(averageScore);
        dashboard.setSavedQuestionsAverageScore(
        savedQuestionsAverageScore);
        dashboard.setPrepNovisMockAverageScore(
        prepNovisMockAverageScore);

        dashboard.setHighestScore(highestScore);
        dashboard.setRecentSessions(recentSessions);

        return dashboard;
    }
}