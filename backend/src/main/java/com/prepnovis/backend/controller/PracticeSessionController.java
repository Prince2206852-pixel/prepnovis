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
import com.prepnovis.backend.dto.response.PracticeSessionResultResponse;
import com.prepnovis.backend.service.PracticeSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Practice Sessions",
        description = "APIs for starting interview practice sessions, submitting answers, completing sessions and viewing results."
)
@RestController
@RequestMapping("/api/v1/practice-sessions")
public class PracticeSessionController {

    private final PracticeSessionService practiceSessionService;

    public PracticeSessionController(
            PracticeSessionService practiceSessionService) {

        this.practiceSessionService = practiceSessionService;
    }

    @Operation(
            summary = "Start a practice session",
            description = "Starts a new practice session using saved questions or PrepNovis Mock Questions based on the selected criteria."
    )
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

    @Operation(
            summary = "Get practice session details",
            description = "Returns the details of a practice session including the questions assigned to that session."
    )
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

    @Operation(
            summary = "Submit an answer",
            description = "Submits and evaluates the user's answer for a question in the selected practice session."
    )
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

    @Operation(
            summary = "Complete a practice session",
            description = "Completes an active practice session and calculates the final session result."
    )
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<PracticeSessionResultResponse> completeSession(
            @PathVariable UUID sessionId,
            Principal principal) {

        String email = principal.getName();

        PracticeSessionResultResponse response =
                practiceSessionService.completeSession(
                        email,
                        sessionId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get practice session result",
            description = "Returns the final result and score details for a completed practice session."
    )
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<PracticeSessionResultResponse> getSessionResult(
            @PathVariable UUID sessionId,
            Principal principal) {

        String email = principal.getName();

        PracticeSessionResultResponse response =
                practiceSessionService.getSessionResult(
                        email,
                        sessionId
                );

        return ResponseEntity.ok(response);
    }
}