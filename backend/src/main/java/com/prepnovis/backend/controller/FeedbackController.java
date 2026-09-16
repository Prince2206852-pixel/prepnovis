package com.prepnovis.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.request.FeedbackRequest;
import com.prepnovis.backend.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<String> sendFeedback(
            @Valid @RequestBody FeedbackRequest request) {

        feedbackService.sendFeedback(request);

        return ResponseEntity.ok(
                "Thank you. Your feedback has been sent successfully."
        );
    }
}