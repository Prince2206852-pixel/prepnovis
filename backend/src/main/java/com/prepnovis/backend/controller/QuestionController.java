package com.prepnovis.backend.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.PageResponse;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.service.QuestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request) {

        QuestionResponse response =
                questionService.createQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
public ResponseEntity<PageResponse<QuestionResponse>> getAllQuestions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String topic,
        @RequestParam(required = false)
        DifficultyLevel difficultyLevel,
        @RequestParam(required = false)
        QuestionType questionType) {

    if (page < 0) {
        throw new IllegalArgumentException(
                "Page number cannot be negative."
        );
    }

    if (size <= 0 || size > 100) {
        throw new IllegalArgumentException(
                "Page size must be between 1 and 100."
        );
    }

    return ResponseEntity.ok(
            questionService.getAllQuestions(
                    page,
                    size,
                    category,
                    topic,
                    difficultyLevel,
                    questionType
            )
    );
}

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                questionService.getQuestionById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequest request) {

        return ResponseEntity.ok(
                questionService.updateQuestion(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }
}