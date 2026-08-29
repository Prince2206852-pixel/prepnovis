package com.prepnovis.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.ai.GeminiClient;
import com.prepnovis.backend.dto.response.GeneratedMockQuestion;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.service.MockQuestionGenerationService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class MockQuestionGenerationServiceImpl
        implements MockQuestionGenerationService {

    private final GeminiClient geminiClient;
    private final JsonMapper jsonMapper;

    public MockQuestionGenerationServiceImpl(
            GeminiClient geminiClient,
            JsonMapper jsonMapper) {

        this.geminiClient = geminiClient;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public List<GeneratedMockQuestion> generateQuestions(
            String category,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionType questionType,
            int totalQuestions) {

        if (totalQuestions < 5 || totalQuestions > 10) {
            throw new IllegalArgumentException(
                    "PrepNovis Mock must contain between 5 and 10 questions."
            );
        }

        String prompt = """
                You are the interview question generation engine for PrepNovis.

                Generate exactly %d interview questions.

                Candidate selected:

                Category: %s
                Topic: %s
                Difficulty: %s
                Question Type: %s

                Generate realistic interview questions that could actually
                be asked to a software developer in a technical interview.

                Questions must:
                - match the selected topic
                - match the selected difficulty
                - avoid duplicate questions
                - be clear and practical
                - not contain answers inside the question
                - be appropriate for interview practice

                For every question also generate a strong reference answer.
                The reference answer is for backend evaluation only.

                Return ONLY valid JSON using exactly this structure:

                {
                  "questions": [
                    {
                      "questionText": "question here",
                      "referenceAnswer": "reference answer here"
                    }
                  ]
                }

                Important:
                - Return exactly %d questions.
                - Return JSON only.
                - Do not return markdown.
                - Do not wrap the response in ```json.
                """
                .formatted(
                        totalQuestions,
                        category,
                        topic,
                        difficultyLevel,
                        questionType,
                        totalQuestions
                );

        try {

            String aiResponse =
                    geminiClient.generateContent(prompt);

            if (aiResponse == null || aiResponse.isBlank()) {
                throw new RuntimeException(
                        "Gemini returned an empty mock question response."
                );
            }

            String cleanedResponse =
                    cleanJsonResponse(aiResponse);

            JsonNode root =
                    jsonMapper.readTree(cleanedResponse);

            JsonNode questionsNode =
                    root.get("questions");

            if (questionsNode == null ||
                    !questionsNode.isArray()) {

                throw new RuntimeException(
                        "Invalid PrepNovis Mock response."
                );
            }

            List<GeneratedMockQuestion> questions =
                    new ArrayList<>();

            for (JsonNode questionNode : questionsNode) {

                JsonNode questionTextNode =
                        questionNode.get("questionText");

                JsonNode referenceAnswerNode =
                        questionNode.get("referenceAnswer");

                if (questionTextNode == null ||
                        referenceAnswerNode == null) {
                    continue;
                }

                String questionText =
                        questionTextNode.asText();

                String referenceAnswer =
                        referenceAnswerNode.asText();

                if (questionText.isBlank() ||
                        referenceAnswer.isBlank()) {
                    continue;
                }

                questions.add(
                        new GeneratedMockQuestion(
                                questionText.trim(),
                                referenceAnswer.trim()
                        )
                );
            }

            if (questions.size() != totalQuestions) {
                throw new RuntimeException(
                        "PrepNovis Mock did not generate the requested number of questions."
                );
            }

            return questions;

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to generate PrepNovis Mock questions.",
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
}