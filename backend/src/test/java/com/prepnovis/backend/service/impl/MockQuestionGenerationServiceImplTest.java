package com.prepnovis.backend.service.impl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prepnovis.backend.ai.GeminiClient;
import com.prepnovis.backend.dto.response.GeneratedMockQuestion;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

import tools.jackson.databind.json.JsonMapper;

class MockQuestionGenerationServiceImplTest {

    private GeminiClient geminiClient;
    private JsonMapper jsonMapper;
    private MockQuestionGenerationServiceImpl service;

    @BeforeEach
    void setUp() {

        geminiClient = mock(GeminiClient.class);

        jsonMapper = JsonMapper.builder().build();

        service =
                new MockQuestionGenerationServiceImpl(
                        geminiClient,
                        jsonMapper
                );
    }

    @Test
    void generateQuestions_ShouldReturnRequestedQuestions_WhenResponseIsValid() {

        String aiResponse = """
                {
                  "questions": [
                    {
                      "questionText": "What is dependency injection?",
                      "referenceAnswer": "Dependency injection provides dependencies from outside."
                    },
                    {
                      "questionText": "What is Spring Boot?",
                      "referenceAnswer": "Spring Boot simplifies Spring application development."
                    },
                    {
                      "questionText": "What is IoC?",
                      "referenceAnswer": "IoC transfers object creation control to the container."
                    },
                    {
                      "questionText": "What is a Spring Bean?",
                      "referenceAnswer": "A Spring Bean is an object managed by the Spring container."
                    },
                    {
                      "questionText": "What is @Autowired?",
                      "referenceAnswer": "@Autowired is used for automatic dependency injection."
                    }
                  ]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        List<GeneratedMockQuestion> result =
                service.generateQuestions(
                        "Java Backend",
                        "Spring Boot",
                        DifficultyLevel.MEDIUM,
                        QuestionType.TECHNICAL,
                        5
                );

        assertNotNull(result);
        assertEquals(5, result.size());

        assertEquals(
                "What is dependency injection?",
                result.get(0).getQuestionText()
        );

        assertEquals(
                "Dependency injection provides dependencies from outside.",
                result.get(0).getReferenceAnswer()
        );

        verify(geminiClient)
                .generateContent(anyString());
    }

    @Test
    void generateQuestions_ShouldSendSelectedConfigurationInPrompt() {

        when(geminiClient.generateContent(anyString()))
                .thenReturn(validFiveQuestionResponse());

        service.generateQuestions(
                "Java Backend",
                "Kafka",
                DifficultyLevel.HARD,
                QuestionType.SYSTEM_DESIGN,
                5
        );

        ArgumentCaptor<String> promptCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(geminiClient)
                .generateContent(promptCaptor.capture());

        String prompt = promptCaptor.getValue();

        assertTrue(prompt.contains("Generate exactly 5 interview questions."));
        assertTrue(prompt.contains("Category: Java Backend"));
        assertTrue(prompt.contains("Topic: Kafka"));
        assertTrue(prompt.contains("Difficulty: HARD"));
        assertTrue(prompt.contains("Question Type: SYSTEM_DESIGN"));
        assertTrue(prompt.contains("Return exactly 5 questions."));
    }

    @Test
    void generateQuestions_ShouldAcceptMinimumFiveQuestions() {

        when(geminiClient.generateContent(anyString()))
                .thenReturn(validFiveQuestionResponse());

        List<GeneratedMockQuestion> result =
                service.generateQuestions(
                        "Java",
                        "Spring",
                        DifficultyLevel.EASY,
                        QuestionType.TECHNICAL,
                        5
                );

        assertEquals(5, result.size());
    }

    @Test
    void generateQuestions_ShouldRejectLessThanFiveQuestions() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.EASY,
                                QuestionType.TECHNICAL,
                                4
                        )
                );

        assertEquals(
                "PrepNovis Mock must contain between 5 and 10 questions.",
                exception.getMessage()
        );

        verify(geminiClient, never())
                .generateContent(anyString());
    }

    @Test
    void generateQuestions_ShouldRejectMoreThanTenQuestions() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.EASY,
                                QuestionType.TECHNICAL,
                                11
                        )
                );

        assertEquals(
                "PrepNovis Mock must contain between 5 and 10 questions.",
                exception.getMessage()
        );

        verify(geminiClient, never())
                .generateContent(anyString());
    }

    @Test
    void generateQuestions_ShouldHandleMarkdownJsonCodeBlock() {

        String aiResponse = """
                ```json
                {
                  "questions": [
                    {
                      "questionText": "Question 1",
                      "referenceAnswer": "Answer 1"
                    },
                    {
                      "questionText": "Question 2",
                      "referenceAnswer": "Answer 2"
                    },
                    {
                      "questionText": "Question 3",
                      "referenceAnswer": "Answer 3"
                    },
                    {
                      "questionText": "Question 4",
                      "referenceAnswer": "Answer 4"
                    },
                    {
                      "questionText": "Question 5",
                      "referenceAnswer": "Answer 5"
                    }
                  ]
                }
                ```
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        List<GeneratedMockQuestion> result =
                service.generateQuestions(
                        "Java",
                        "Spring",
                        DifficultyLevel.MEDIUM,
                        QuestionType.TECHNICAL,
                        5
                );

        assertEquals(5, result.size());

        assertEquals(
                "Question 1",
                result.get(0).getQuestionText()
        );
    }

    @Test
    void generateQuestions_ShouldTrimQuestionAndReferenceAnswer() {

        String aiResponse = """
                {
                  "questions": [
                    {
                      "questionText": "  Question 1  ",
                      "referenceAnswer": "  Answer 1  "
                    },
                    {
                      "questionText": "Question 2",
                      "referenceAnswer": "Answer 2"
                    },
                    {
                      "questionText": "Question 3",
                      "referenceAnswer": "Answer 3"
                    },
                    {
                      "questionText": "Question 4",
                      "referenceAnswer": "Answer 4"
                    },
                    {
                      "questionText": "Question 5",
                      "referenceAnswer": "Answer 5"
                    }
                  ]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        List<GeneratedMockQuestion> result =
                service.generateQuestions(
                        "Java",
                        "Spring",
                        DifficultyLevel.MEDIUM,
                        QuestionType.TECHNICAL,
                        5
                );

        assertEquals(
                "Question 1",
                result.get(0).getQuestionText()
        );

        assertEquals(
                "Answer 1",
                result.get(0).getReferenceAnswer()
        );
    }

    @Test
    void generateQuestions_ShouldThrowException_WhenGeminiReturnsEmptyResponse() {

        when(geminiClient.generateContent(anyString()))
                .thenReturn("");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Gemini returned an empty mock question response.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void generateQuestions_ShouldThrowException_WhenQuestionsArrayIsMissing() {

        String aiResponse = """
                {
                  "message": "No questions"
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Invalid PrepNovis Mock response.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void generateQuestions_ShouldThrowException_WhenGeneratedCountDoesNotMatchRequestedCount() {

        String aiResponse = """
                {
                  "questions": [
                    {
                      "questionText": "Question 1",
                      "referenceAnswer": "Answer 1"
                    },
                    {
                      "questionText": "Question 2",
                      "referenceAnswer": "Answer 2"
                    }
                  ]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "PrepNovis Mock did not generate the requested number of questions.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void generateQuestions_ShouldIgnoreInvalidQuestions_AndFailCountValidation() {

        String aiResponse = """
                {
                  "questions": [
                    {
                      "questionText": "Question 1",
                      "referenceAnswer": "Answer 1"
                    },
                    {
                      "questionText": "",
                      "referenceAnswer": "Answer 2"
                    },
                    {
                      "questionText": "Question 3"
                    },
                    {
                      "questionText": "Question 4",
                      "referenceAnswer": ""
                    },
                    {
                      "questionText": "Question 5",
                      "referenceAnswer": "Answer 5"
                    }
                  ]
                }
                """;

        when(geminiClient.generateContent(anyString()))
                .thenReturn(aiResponse);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "PrepNovis Mock did not generate the requested number of questions.",
                exception.getCause().getMessage()
        );
    }

    @Test
    void generateQuestions_ShouldThrowException_WhenGeminiReturnsMalformedJson() {

        when(geminiClient.generateContent(anyString()))
                .thenReturn(
                        "{ this is not valid json }"
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());
    }

    @Test
    void generateQuestions_ShouldThrowSafeException_WhenGeminiClientFails() {

        when(geminiClient.generateContent(anyString()))
                .thenThrow(
                        new RuntimeException(
                                "Gemini API unavailable"
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateQuestions(
                                "Java",
                                "Spring",
                                DifficultyLevel.MEDIUM,
                                QuestionType.TECHNICAL,
                                5
                        )
                );

        assertEquals(
                "Failed to generate PrepNovis Mock questions.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        assertEquals(
                "Gemini API unavailable",
                exception.getCause().getMessage()
        );
    }

    private String validFiveQuestionResponse() {

        return """
                {
                  "questions": [
                    {
                      "questionText": "Question 1",
                      "referenceAnswer": "Answer 1"
                    },
                    {
                      "questionText": "Question 2",
                      "referenceAnswer": "Answer 2"
                    },
                    {
                      "questionText": "Question 3",
                      "referenceAnswer": "Answer 3"
                    },
                    {
                      "questionText": "Question 4",
                      "referenceAnswer": "Answer 4"
                    },
                    {
                      "questionText": "Question 5",
                      "referenceAnswer": "Answer 5"
                    }
                  ]
                }
                """;
    }
}