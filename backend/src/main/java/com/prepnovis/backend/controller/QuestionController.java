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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Questions",
        description = "APIs for creating, viewing, filtering, updating and deleting saved interview questions."
)
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(
            summary = "Create a saved question",
            description = "Creates a new interview question for the authenticated user."
    )
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request) {

        QuestionResponse response =
                questionService.createQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get saved questions",
            description = "Returns saved interview questions with pagination and optional filters."
    )
    @GetMapping
    public ResponseEntity<PageResponse<QuestionResponse>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false) QuestionType questionType) {

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

    @Operation(
            summary = "Get a question by ID",
            description = "Returns a saved interview question using its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                questionService.getQuestionById(id)
        );
    }

    @Operation(
            summary = "Update a saved question",
            description = "Updates an existing saved interview question."
    )
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequest request) {

        return ResponseEntity.ok(
                questionService.updateQuestion(id, request)
        );
    }

    @Operation(
            summary = "Delete a saved question",
            description = "Deletes an existing saved interview question."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }
}