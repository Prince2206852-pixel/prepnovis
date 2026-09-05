package com.prepnovis.backend.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prepnovis.backend.dto.request.QuestionRequest;
import com.prepnovis.backend.dto.response.QuestionResponse;
import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;
import com.prepnovis.backend.exception.QuestionNotFoundException;
import com.prepnovis.backend.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Test
    void createQuestion_ShouldCreateQuestionSuccessfully() {

        QuestionRequest request = new QuestionRequest();
        request.setQuestionText("What is dependency injection?");
        request.setAnswer("Dependency injection provides required dependencies from outside.");
        request.setCategory("Java");
        request.setTopic("Spring Boot");
        request.setQuestionType(QuestionType.TECHNICAL);
        request.setDifficultyLevel(DifficultyLevel.MEDIUM);
        request.setTags("spring,di");

        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation -> {

                    Question question = invocation.getArgument(0);
                    question.setId(UUID.randomUUID());

                    return question;
                });

        QuestionResponse response =
                questionService.createQuestion(request);

        assertEquals(
                "What is dependency injection?",
                response.getQuestionText()
        );

        assertEquals(
                "Dependency injection provides required dependencies from outside.",
                response.getAnswer()
        );

        assertEquals("Java", response.getCategory());
        assertEquals("Spring Boot", response.getTopic());
        assertEquals(
                QuestionType.TECHNICAL,
                response.getQuestionType()
        );
        assertEquals(
                DifficultyLevel.MEDIUM,
                response.getDifficultyLevel()
        );
        assertEquals("spring,di", response.getTags());

        verify(questionRepository)
                .save(any(Question.class));
    }

    @Test
void getQuestionById_ShouldReturnQuestionSuccessfully() {

    UUID questionId = UUID.randomUUID();

    Question question = new Question();
    question.setId(questionId);
    question.setQuestionText("What is dependency injection?");
    question.setAnswer("Dependency injection provides dependencies from outside.");
    question.setCategory("Java");
    question.setTopic("Spring Boot");
    question.setQuestionType(QuestionType.TECHNICAL);
    question.setDifficultyLevel(DifficultyLevel.MEDIUM);
    question.setTags("spring,di");

    when(questionRepository.findById(questionId))
            .thenReturn(Optional.of(question));

    QuestionResponse response =
            questionService.getQuestionById(questionId);

    assertEquals(questionId, response.getId());
    assertEquals(
            "What is dependency injection?",
            response.getQuestionText()
    );
    assertEquals("Java", response.getCategory());
    assertEquals("Spring Boot", response.getTopic());
    assertEquals(
            QuestionType.TECHNICAL,
            response.getQuestionType()
    );
    assertEquals(
            DifficultyLevel.MEDIUM,
            response.getDifficultyLevel()
    );

    verify(questionRepository)
            .findById(questionId);
}
@Test
void getQuestionById_ShouldThrowException_WhenQuestionDoesNotExist() {

    UUID questionId = UUID.randomUUID();

    when(questionRepository.findById(questionId))
            .thenReturn(Optional.empty());

    QuestionNotFoundException exception =
            assertThrows(
                    QuestionNotFoundException.class,
                    () -> questionService.getQuestionById(questionId)
            );

    assertEquals(
            "Question not found.",
            exception.getMessage()
    );

    verify(questionRepository)
            .findById(questionId);
}
@Test
void updateQuestion_ShouldUpdateQuestionSuccessfully() {

    UUID questionId = UUID.randomUUID();

    Question existingQuestion = new Question();
    existingQuestion.setId(questionId);
    existingQuestion.setQuestionText("Old question");
    existingQuestion.setAnswer("Old answer");
    existingQuestion.setCategory("Java");
    existingQuestion.setTopic("Core Java");
    existingQuestion.setQuestionType(QuestionType.TECHNICAL);
    existingQuestion.setDifficultyLevel(DifficultyLevel.EASY);
    existingQuestion.setTags("java");

    QuestionRequest request = new QuestionRequest();
    request.setQuestionText("What is dependency injection?");
    request.setAnswer("Dependencies are provided from outside.");
    request.setCategory("Java");
    request.setTopic("Spring Boot");
    request.setQuestionType(QuestionType.TECHNICAL);
    request.setDifficultyLevel(DifficultyLevel.MEDIUM);
    request.setTags("spring,di");

    when(questionRepository.findById(questionId))
            .thenReturn(Optional.of(existingQuestion));

    when(questionRepository.save(any(Question.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    QuestionResponse response =
            questionService.updateQuestion(questionId, request);

    assertEquals(questionId, response.getId());
    assertEquals(
            "What is dependency injection?",
            response.getQuestionText()
    );
    assertEquals(
            "Dependencies are provided from outside.",
            response.getAnswer()
    );
    assertEquals("Spring Boot", response.getTopic());
    assertEquals(
            DifficultyLevel.MEDIUM,
            response.getDifficultyLevel()
    );
    assertEquals("spring,di", response.getTags());

    verify(questionRepository)
            .findById(questionId);

    verify(questionRepository)
            .save(existingQuestion);
}
@Test
void deleteQuestion_ShouldDeleteQuestionSuccessfully() {

    UUID questionId = UUID.randomUUID();

    Question question = new Question();
    question.setId(questionId);
    question.setQuestionText("What is dependency injection?");

    when(questionRepository.findById(questionId))
            .thenReturn(Optional.of(question));

    questionService.deleteQuestion(questionId);

    verify(questionRepository)
            .findById(questionId);

    verify(questionRepository)
            .delete(question);
}

}