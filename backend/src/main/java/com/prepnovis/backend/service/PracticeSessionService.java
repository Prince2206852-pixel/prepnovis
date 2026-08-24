package com.prepnovis.backend.service;

import java.util.UUID;

import com.prepnovis.backend.dto.request.StartPracticeSessionRequest;
import com.prepnovis.backend.dto.request.SubmitPracticeAnswerRequest;
import com.prepnovis.backend.dto.response.PracticeSessionDetailResponse;
import com.prepnovis.backend.dto.response.PracticeSessionQuestionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResultResponse;


public interface PracticeSessionService {

    PracticeSessionResponse startSession(
            String email,
            StartPracticeSessionRequest request);

            PracticeSessionDetailResponse getSessionDetails(
        String email,
        UUID sessionId);

        PracticeSessionQuestionResponse submitAnswer(
        String email,
        UUID sessionId,
        UUID sessionQuestionId,
        SubmitPracticeAnswerRequest request);

        PracticeSessionResultResponse completeSession(
        String email,
        UUID sessionId);

        PracticeSessionResultResponse getSessionResult(
        String email,
        UUID sessionId);
}