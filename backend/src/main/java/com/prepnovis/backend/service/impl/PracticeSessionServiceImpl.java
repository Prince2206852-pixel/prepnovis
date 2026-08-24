package com.prepnovis.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.request.StartPracticeSessionRequest;
import com.prepnovis.backend.dto.request.SubmitPracticeAnswerRequest;
import com.prepnovis.backend.dto.response.PracticeSessionDetailResponse;
import com.prepnovis.backend.dto.response.PracticeSessionQuestionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResultResponse;
import com.prepnovis.backend.entity.PracticeSession;
import com.prepnovis.backend.entity.PracticeSessionQuestion;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.PracticeSessionStatus;
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
import com.prepnovis.backend.service.PracticeSessionService;

@Service
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final PracticeSessionRepository practiceSessionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PracticeSessionQuestionRepository practiceSessionQuestionRepository;

    public PracticeSessionServiceImpl(
        PracticeSessionRepository practiceSessionRepository,
        UserRepository userRepository,
        QuestionRepository questionRepository,
        PracticeSessionQuestionRepository practiceSessionQuestionRepository) {

    this.practiceSessionRepository = practiceSessionRepository;
    this.userRepository = userRepository;
    this.questionRepository = questionRepository;
    this.practiceSessionQuestionRepository = practiceSessionQuestionRepository;
}

    @Override
public PracticeSessionResponse startSession(
        String email,
        StartPracticeSessionRequest request) {

    // 1. Find logged-in user
    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UserNotFoundException("User not found.")
            );

    // 2. Create session
    PracticeSession session = new PracticeSession();

    session.setUser(user);
    session.setCategory(request.getCategory());
    session.setTopic(request.getTopic());
    session.setDifficultyLevel(request.getDifficultyLevel());
    session.setQuestionType(request.getQuestionType());
    session.setTotalQuestions(request.getTotalQuestions());
    session.setStatus(PracticeSessionStatus.IN_PROGRESS);

    // 3. Save session
    PracticeSession savedSession =
            practiceSessionRepository.save(session);


    // 4. Find matching questions
    List<Question> matchingQuestions =
            questionRepository
                    .findByCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
                            request.getCategory(),
                            request.getTopic(),
                            request.getDifficultyLevel(),
                            request.getQuestionType()
                    );


    // 5. Select requested number of questions
    List<Question> selectedQuestions =
            matchingQuestions.stream()
                    .limit(request.getTotalQuestions())
                    .toList();
                    


    // 6. Attach selected questions to this session
    for (Question question : selectedQuestions) {

        PracticeSessionQuestion sessionQuestion =
                new PracticeSessionQuestion();

        sessionQuestion.setPracticeSession(savedSession);
        sessionQuestion.setQuestion(question);
        sessionQuestion.setAnswered(false);

        practiceSessionQuestionRepository.save(sessionQuestion);
    }


    // 7. Prepare response
    PracticeSessionResponse response =
            new PracticeSessionResponse();

response.setId(savedSession.getId());
response.setCategory(savedSession.getCategory());
response.setTopic(savedSession.getTopic());
response.setDifficultyLevel(savedSession.getDifficultyLevel());
response.setQuestionType(savedSession.getQuestionType());
response.setTotalQuestions(savedSession.getTotalQuestions());
response.setAssignedQuestions(selectedQuestions.size());
response.setStatus(savedSession.getStatus());
response.setCreatedAt(savedSession.getCreatedAt());

return response;

}

@Override
public PracticeSessionDetailResponse getSessionDetails(
        String email,
        UUID sessionId) {

    PracticeSession session =
            practiceSessionRepository.findById(sessionId)
                    .orElseThrow(() ->
                    new PracticeSessionNotFoundException( "Practice session not found.")
                    );

    if (!session.getUser().getEmail().equals(email)) {
        throw new PracticeSessionAccessDeniedException("You are not allowed to access this session.");
    }

    List<PracticeSessionQuestion> sessionQuestions =
            practiceSessionQuestionRepository
                    .findByPracticeSessionId(sessionId);

    List<PracticeSessionQuestionResponse> questionResponses =
            sessionQuestions.stream()
                    .map(sessionQuestion -> {

                        Question question =
                                sessionQuestion.getQuestion();

                        PracticeSessionQuestionResponse response =
                                new PracticeSessionQuestionResponse();

                        response.setId(sessionQuestion.getId());
                        response.setQuestionId(question.getId());
                        response.setQuestionText(question.getQuestionText());
                        response.setCategory(question.getCategory());
                        response.setTopic(question.getTopic());
                        response.setQuestionType(
                                question.getQuestionType().name()
                        );
                        response.setDifficultyLevel(
                                question.getDifficultyLevel().name()
                        );

                        response.setAnswered(
                                sessionQuestion.getAnswered()
                        );
                        response.setUserAnswer(
                                sessionQuestion.getUserAnswer()
                        );
                        response.setScore(
                                sessionQuestion.getScore()
                        );
                        response.setFeedback(
                                sessionQuestion.getFeedback()
                        );

                        return response;
                    })
                    .toList();

    PracticeSessionDetailResponse response =
            new PracticeSessionDetailResponse();

    response.setId(session.getId());
    response.setCategory(session.getCategory());
    response.setTopic(session.getTopic());
    response.setDifficultyLevel(session.getDifficultyLevel());
    response.setQuestionType(session.getQuestionType());
    response.setTotalQuestions(session.getTotalQuestions());
    response.setStatus(session.getStatus());
    response.setCreatedAt(session.getCreatedAt());
    response.setQuestions(questionResponses);

    return response;
}

@Override
public PracticeSessionQuestionResponse submitAnswer(
        String email,
        UUID sessionId,
        UUID sessionQuestionId,
        SubmitPracticeAnswerRequest request) {

    // Step 1: Find practice session
    PracticeSession session =
            practiceSessionRepository.findById(sessionId)
                    .orElseThrow(() ->
                     new PracticeSessionNotFoundException("Practice session not found.")
                    );

    // Step 2: Verify session belongs to logged-in user
    if (!session.getUser().getEmail().equals(email)) {
            throw new PracticeSessionAccessDeniedException("You are not allowed to access this session."
            );
    }

    // Step 3: Find session question
    PracticeSessionQuestion sessionQuestion =
            practiceSessionQuestionRepository
                    .findById(sessionQuestionId)
                    .orElseThrow(() ->
                    new PracticeSessionQuestionNotFoundException("Practice session question not found.")
                    );

    // Step 4: Verify question belongs to this session
    if (!sessionQuestion
            .getPracticeSession()
            .getId()
            .equals(sessionId)) {

        throw new InvalidPracticeSessionQuestionException("Question does not belong to this practice session.");
    }

    // Step 5: Save user's answer
    sessionQuestion.setUserAnswer(request.getAnswer());
    sessionQuestion.setAnswered(true);

    PracticeSessionQuestion savedQuestion =
            practiceSessionQuestionRepository.save(sessionQuestion);

    // Step 6: Prepare response
    Question question = savedQuestion.getQuestion();

    PracticeSessionQuestionResponse response =
            new PracticeSessionQuestionResponse();

    response.setId(savedQuestion.getId());
    response.setQuestionId(question.getId());
    response.setQuestionText(question.getQuestionText());
    response.setCategory(question.getCategory());
    response.setTopic(question.getTopic());
    response.setQuestionType(
            question.getQuestionType().name()
    );
    response.setDifficultyLevel(
            question.getDifficultyLevel().name()
    );
    response.setAnswered(savedQuestion.getAnswered());
    response.setUserAnswer(savedQuestion.getUserAnswer());
    response.setScore(savedQuestion.getScore());
    response.setFeedback(savedQuestion.getFeedback());

    return response;
}

@Override
public PracticeSessionResultResponse completeSession(
        String email,
        UUID sessionId) {

    // Step 1: Find session
    PracticeSession session =
            practiceSessionRepository.findById(sessionId)
                    .orElseThrow(() ->
                            new PracticeSessionNotFoundException(
                                    "Practice session not found."
                            )
                    );

    // Step 2: Verify ownership
    if (!session.getUser().getEmail().equals(email)) {
        throw new PracticeSessionAccessDeniedException(
                "You are not allowed to access this session."
        );
    }

    if (session.getStatus() == PracticeSessionStatus.COMPLETED) {
    throw new InvalidPracticeSessionStateException(
            "Practice session is already completed."
    );
}

    // Step 3: Get assigned questions
    List<PracticeSessionQuestion> sessionQuestions =
            practiceSessionQuestionRepository
                    .findByPracticeSessionId(sessionId);

    int assignedQuestions = sessionQuestions.size();

    int answeredQuestions = (int) sessionQuestions.stream()
            .filter(question -> Boolean.TRUE.equals(question.getAnswered()))
            .count();

    int unansweredQuestions =
            assignedQuestions - answeredQuestions;

    // Step 4: Calculate average score only from scored questions
    List<Double> scores = sessionQuestions.stream()
            .map(PracticeSessionQuestion::getScore)
            .filter(score -> score != null)
            .toList();

    Double averageScore = null;

    if (!scores.isEmpty()) {
        averageScore = scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // Step 5: Complete session
    session.setStatus(PracticeSessionStatus.COMPLETED);
    session.setCompletedAt(LocalDateTime.now());

    PracticeSession completedSession =
            practiceSessionRepository.save(session);

    // Step 6: Prepare result
    PracticeSessionResultResponse response =
            new PracticeSessionResultResponse();

    response.setSessionId(completedSession.getId());
    response.setTotalQuestions(completedSession.getTotalQuestions());
    response.setAssignedQuestions(assignedQuestions);
    response.setAnsweredQuestions(answeredQuestions);
    response.setUnansweredQuestions(unansweredQuestions);
    response.setAverageScore(averageScore);
    response.setStatus(completedSession.getStatus());
    response.setCompletedAt(completedSession.getCompletedAt());

    return response;
}

@Override
public PracticeSessionResultResponse getSessionResult(
        String email,
        UUID sessionId) {

    // Step 1: Find session
    PracticeSession session =
            practiceSessionRepository.findById(sessionId)
                    .orElseThrow(() ->
                            new PracticeSessionNotFoundException(
                                    "Practice session not found."
                            )
                    );

    // Step 2: Verify ownership
    if (!session.getUser().getEmail().equals(email)) {
        throw new PracticeSessionAccessDeniedException(
                "You are not allowed to access this session."
        );
    }

    if (session.getStatus() != PracticeSessionStatus.COMPLETED) {
    throw new InvalidPracticeSessionStateException(
            "Practice session is not completed yet."
    );
}

    // Step 3: Get assigned questions
    List<PracticeSessionQuestion> sessionQuestions =
            practiceSessionQuestionRepository
                    .findByPracticeSessionId(sessionId);

    int assignedQuestions = sessionQuestions.size();

    int answeredQuestions = (int) sessionQuestions.stream()
            .filter(question ->
                    Boolean.TRUE.equals(question.getAnswered()))
            .count();

    int unansweredQuestions =
            assignedQuestions - answeredQuestions;

    // Step 4: Calculate score if scores exist
    List<Double> scores = sessionQuestions.stream()
            .map(PracticeSessionQuestion::getScore)
            .filter(score -> score != null)
            .toList();

    Double averageScore = null;

    if (!scores.isEmpty()) {
        averageScore = scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // Step 5: Prepare response
    PracticeSessionResultResponse response =
            new PracticeSessionResultResponse();

    response.setSessionId(session.getId());
    response.setTotalQuestions(session.getTotalQuestions());
    response.setAssignedQuestions(assignedQuestions);
    response.setAnsweredQuestions(answeredQuestions);
    response.setUnansweredQuestions(unansweredQuestions);
    response.setAverageScore(averageScore);
    response.setStatus(session.getStatus());
    response.setCompletedAt(session.getCompletedAt());

    return response;
}

}