package com.prepnovis.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prepnovis.backend.ai.GeminiClient;
import com.prepnovis.backend.dto.response.AnswerEvaluationResult;

import tools.jackson.databind.json.JsonMapper;

class AnswerEvaluationServiceImplTest {

    private GeminiClient geminiClient;
    private JsonMapper jsonMapper;
    private AnswerEvaluationServiceImpl answerEvaluationService;

    @BeforeEach
    void setUp() {

        geminiClient = mock(GeminiClient.class);

        jsonMapper = JsonMapper.builder().build();

        answerEvaluationService =
                new AnswerEvaluationServiceImpl(
                        geminiClient,
                        jsonMapper
                );
    }

    @Test
    void evaluateAnswer_ShouldReturnEvaluation_WhenGeminiReturnsValidJson() {

        String aiResponse = """
                {
                  "score": 8.5,
                  "feedback": "Good explanation with minor gaps.",
                  "strengths": [
                    "Correct core concept",
                    "Clear explanation"
                  ],
                  "improvements": [
                    "Add one practical example"
                  ]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        AnswerEvaluationResult result =
                answerEvaluationService.evaluateAnswer(
                        "What is dependency injection?",
                        "Dependency injection provides dependencies from outside.",
                        "Spring provides required objects to classes."
                );

        assertNotNull(result);

        assertEquals(
                8.5,
                result.getScore(),
                0.001
        );

        assertEquals(
                "Good explanation with minor gaps.",
                result.getFeedback()
        );

        assertEquals(
                2,
                result.getStrengths().size()
        );

        assertEquals(
                "Correct core concept",
                result.getStrengths().get(0)
        );

        assertEquals(
                "Clear explanation",
                result.getStrengths().get(1)
        );

        assertEquals(
                1,
                result.getImprovements().size()
        );

        assertEquals(
                "Add one practical example",
                result.getImprovements().get(0)
        );

        verify(geminiClient)
                .generateContent(anyString());
    }

    @Test
    void evaluateAnswer_ShouldSendQuestionReferenceAndUserAnswerToGemini() {

        String aiResponse = """
                {
                  "score": 7.0,
                  "feedback": "Good answer.",
                  "strengths": ["Correct"],
                  "improvements": ["Add detail"]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        answerEvaluationService.evaluateAnswer(
                "What is Kafka?",
                "Kafka is a distributed event streaming platform.",
                "Kafka is mainly used for asynchronous communication."
        );

        ArgumentCaptor<String> promptCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(geminiClient)
                .generateContent(promptCaptor.capture());

        String prompt = promptCaptor.getValue();

        assertTrue(
                prompt.contains("What is Kafka?")
        );

        assertTrue(
                prompt.contains(
                        "Kafka is a distributed event streaming platform."
                )
        );

        assertTrue(
                prompt.contains(
                        "Kafka is mainly used for asynchronous communication."
                )
        );
    }

    @Test
    void evaluateAnswer_ShouldHandleMarkdownJsonCodeBlock() {

        String aiResponse = """
                ```json
                {
                  "score": 9.0,
                  "feedback": "Strong answer.",
                  "strengths": ["Accurate"],
                  "improvements": ["Add example"]
                }
                ```
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        AnswerEvaluationResult result =
                answerEvaluationService.evaluateAnswer(
                        "Question",
                        "Reference",
                        "Answer"
                );

        assertNotNull(result);

        assertEquals(
                9.0,
                result.getScore(),
                0.001
        );

        assertEquals(
                "Strong answer.",
                result.getFeedback()
        );
    }

    @Test
    void evaluateAnswer_ShouldClampScoreToTen_WhenScoreIsAboveTen() {

        String aiResponse = """
                {
                  "score": 15.0,
                  "feedback": "Excellent.",
                  "strengths": ["Strong answer"],
                  "improvements": ["None"]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        AnswerEvaluationResult result =
                answerEvaluationService.evaluateAnswer(
                        "Question",
                        "Reference",
                        "Answer"
                );

        assertEquals(
                10.0,
                result.getScore(),
                0.001
        );
    }

    @Test
    void evaluateAnswer_ShouldClampScoreToZero_WhenScoreIsBelowZero() {

        String aiResponse = """
                {
                  "score": -4.0,
                  "feedback": "Incorrect answer.",
                  "strengths": ["Attempted answer"],
                  "improvements": ["Review the concept"]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        AnswerEvaluationResult result =
                answerEvaluationService.evaluateAnswer(
                        "Question",
                        "Reference",
                        "Answer"
                );

        assertEquals(
                0.0,
                result.getScore(),
                0.001
        );
    }

    @Test
    void evaluateAnswer_ShouldAddFallbackValues_WhenListsAreEmpty() {

        String aiResponse = """
                {
                  "score": 5.0,
                  "feedback": "Average answer.",
                  "strengths": [],
                  "improvements": []
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        AnswerEvaluationResult result =
                answerEvaluationService.evaluateAnswer(
                        "Question",
                        "Reference",
                        "Answer"
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getStrengths().size()
        );

        assertEquals(
                "No specific strength was identified.",
                result.getStrengths().get(0)
        );

        assertEquals(
                1,
                result.getImprovements().size()
        );

        assertEquals(
                "Provide a more complete and structured answer.",
                result.getImprovements().get(0)
        );
    }

    @Test
    void evaluateAnswer_ShouldThrowException_WhenGeminiReturnsEmptyResponse() {

        when(geminiClient.generateContent(anyString()))
                .thenReturn("");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> answerEvaluationService.evaluateAnswer(
                                "Question",
                                "Reference",
                                "Answer"
                        )
                );

        assertEquals(
                "Failed to evaluate answer using Gemini.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Gemini returned an empty evaluation response.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void evaluateAnswer_ShouldThrowException_WhenRequiredFieldIsMissing() {

        String aiResponse = """
                {
                  "feedback": "Good answer.",
                  "strengths": ["Correct"],
                  "improvements": ["Add detail"]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> answerEvaluationService.evaluateAnswer(
                                "Question",
                                "Reference",
                                "Answer"
                        )
                );

        assertEquals(
                "Failed to evaluate answer using Gemini.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Gemini response does not contain score.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void evaluateAnswer_ShouldThrowException_WhenGeminiReturnsMalformedJson() {

        String aiResponse =
                "{ this is not valid json }";

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> answerEvaluationService.evaluateAnswer(
                                "Question",
                                "Reference",
                                "Answer"
                        )
                );

        assertEquals(
                "Failed to evaluate answer using Gemini.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());
    }

    @Test
    void evaluateAnswer_ShouldThrowSafeException_WhenGeminiClientFails() {

        when(geminiClient.generateContent(anyString()))
                .thenThrow(
                        new RuntimeException(
                                "Gemini API unavailable"
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> answerEvaluationService.evaluateAnswer(
                                "Question",
                                "Reference",
                                "Answer"
                        )
                );

        assertEquals(
                "Failed to evaluate answer using Gemini.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Gemini API unavailable",
                exception.getCause().getMessage()
        );
    }
}