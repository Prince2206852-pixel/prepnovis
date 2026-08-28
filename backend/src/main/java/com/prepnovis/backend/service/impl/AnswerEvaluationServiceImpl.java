package com.prepnovis.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.ai.GeminiClient;
import com.prepnovis.backend.dto.response.AnswerEvaluationResult;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.service.AnswerEvaluationService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class AnswerEvaluationServiceImpl
        implements AnswerEvaluationService {

    private final GeminiClient geminiClient;
    private final JsonMapper jsonMapper;

    public AnswerEvaluationServiceImpl(
            GeminiClient geminiClient,
            JsonMapper jsonMapper) {

        this.geminiClient = geminiClient;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(
            Question question,
            String userAnswer) {

        String prompt = """
                You are an expert technical interview evaluator.

                Evaluate the candidate's answer fairly and realistically,
                like an experienced technical interviewer.

                Question:
                %s

                Reference Answer:
                %s

                Candidate Answer:
                %s

                Return ONLY valid JSON.

                Use exactly this structure:

                {
                  "score": 0.0,
                  "feedback": "overall feedback",
                  "strengths": [
                    "strength 1",
                    "strength 2"
                  ],
                  "improvements": [
                    "improvement 1",
                    "improvement 2"
                  ]
                }

                Evaluation rules:

                - Score must be between 0 and 10.
                - Evaluate correctness.
                - Evaluate completeness.
                - Evaluate clarity.
                - Evaluate whether the answer sounds suitable for an interview.
                - Do not give high scores just because some keywords are present.
                - Incorrect or irrelevant answers should receive a low score.
                - Partially correct answers should receive a moderate score.
                - Strong, clear and complete answers should receive a high score.
                - Feedback should be concise and useful.
                - Strengths should mention what the candidate did well.
                - Improvements should explain what should be added or corrected.
                - Return JSON only.
                - Do not return markdown.
                - Do not wrap JSON inside ```json blocks.
                """
                .formatted(
                        question.getQuestionText(),
                        question.getAnswer(),
                        userAnswer
                );

        try {

            String aiResponse =
                    geminiClient.generateContent(prompt);

            if (aiResponse == null || aiResponse.isBlank()) {
                throw new RuntimeException(
                        "Gemini returned an empty evaluation response."
                );
            }

            String cleanedResponse =
                    cleanJsonResponse(aiResponse);

            JsonNode json =
                    jsonMapper.readTree(cleanedResponse);

            if (json == null) {
                throw new RuntimeException(
                        "Unable to parse Gemini evaluation response."
                );
            }

            validateRequiredFields(json);

            double score =
                    json.get("score").asDouble();

            score = clampScore(score);

            String feedback =
                    json.get("feedback").asText();

            List<String> strengths =
                    extractStringList(
                            json.get("strengths")
                    );

            List<String> improvements =
                    extractStringList(
                            json.get("improvements")
                    );

            if (strengths.isEmpty()) {
                strengths.add(
                        "No specific strength was identified."
                );
            }

            if (improvements.isEmpty()) {
                improvements.add(
                        "Provide a more complete and structured answer."
                );
            }

            AnswerEvaluationResult result =
                    new AnswerEvaluationResult();

            result.setScore(score);
            result.setFeedback(feedback);
            result.setStrengths(strengths);
            result.setImprovements(improvements);

            return result;

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to evaluate answer using Gemini.",
                    ex
            );
        }
    }

    private String cleanJsonResponse(
            String response) {

        String cleaned =
                response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned =
                    cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned =
                    cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    );
        }

        return cleaned.trim();
    }

    private void validateRequiredFields(
            JsonNode json) {

        if (!json.hasNonNull("score")) {
            throw new RuntimeException(
                    "Gemini response does not contain score."
            );
        }

        if (!json.hasNonNull("feedback")) {
            throw new RuntimeException(
                    "Gemini response does not contain feedback."
            );
        }

        if (!json.hasNonNull("strengths")) {
            throw new RuntimeException(
                    "Gemini response does not contain strengths."
            );
        }

        if (!json.hasNonNull("improvements")) {
            throw new RuntimeException(
                    "Gemini response does not contain improvements."
            );
        }
    }

    private double clampScore(
            double score) {

        if (score < 0) {
            return 0.0;
        }

        if (score > 10) {
            return 10.0;
        }

        return score;
    }

    private List<String> extractStringList(
            JsonNode node) {

        List<String> values =
                new ArrayList<>();

        if (node == null || !node.isArray()) {
            return values;
        }

        for (JsonNode item : node) {

            String value =
                    item.asText();

            if (value != null &&
                    !value.isBlank()) {

                values.add(
                        value.trim()
                );
            }
        }

        return values;
    }
}