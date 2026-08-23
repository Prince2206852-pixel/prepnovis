package com.prepnovis.backend.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.request.StartPracticeSessionRequest;
import com.prepnovis.backend.dto.request.SubmitPracticeAnswerRequest;
import com.prepnovis.backend.dto.response.PracticeSessionDetailResponse;
import com.prepnovis.backend.dto.response.PracticeSessionQuestionResponse;
import com.prepnovis.backend.dto.response.PracticeSessionResponse;
import com.prepnovis.backend.service.PracticeSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/practice-sessions")
public class PracticeSessionController {

    private final PracticeSessionService practiceSessionService;

    public PracticeSessionController(
            PracticeSessionService practiceSessionService) {

        this.practiceSessionService = practiceSessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<PracticeSessionResponse> startSession(
            @Valid @RequestBody StartPracticeSessionRequest request,
            Principal principal) {

        String email = principal.getName();

        PracticeSessionResponse response =
                practiceSessionService.startSession(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{sessionId}")
public ResponseEntity<PracticeSessionDetailResponse> getSessionDetails(
        @PathVariable UUID sessionId,
        Principal principal) {

    String email = principal.getName();

    PracticeSessionDetailResponse response =
            practiceSessionService.getSessionDetails(
                    email,
                    sessionId
            );

    return ResponseEntity.ok(response);
}

@PostMapping("/{sessionId}/questions/{sessionQuestionId}/answer")
public ResponseEntity<PracticeSessionQuestionResponse> submitAnswer(
        @PathVariable UUID sessionId,
        @PathVariable UUID sessionQuestionId,
        @Valid @RequestBody SubmitPracticeAnswerRequest request,
        Principal principal) {

    String email = principal.getName();

    PracticeSessionQuestionResponse response =
            practiceSessionService.submitAnswer(
                    email,
                    sessionId,
                    sessionQuestionId,
                    request
            );

    return ResponseEntity.ok(response);
}

}