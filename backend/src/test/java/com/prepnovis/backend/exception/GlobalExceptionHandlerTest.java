package com.prepnovis.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.prepnovis.backend.dto.response.ExceptionResponse;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {

        globalExceptionHandler = new GlobalExceptionHandler();

        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/test");
    }

    @Test
    void handleEmailAlreadyExistsException_ShouldReturn409() {

        EmailAlreadyExistsException exception =
                new EmailAlreadyExistsException(
                        "Email is already registered."
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleEmailAlreadyExistsException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                409,
                "Conflict",
                "Email is already registered."
        );
    }

    @Test
    void handleInvalidCredentialsException_ShouldReturn401() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException(
                        "Invalid email or password."
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleInvalidCredentialsException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                401,
                "Unauthorized",
                "Invalid email or password."
        );
    }

    @Test
    void handleQuestionNotFoundException_ShouldReturn404() {

        QuestionNotFoundException exception =
                new QuestionNotFoundException(
                        "Question not found."
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleQuestionNotFoundException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                404,
                "Not Found",
                "Question not found."
        );
    }

    @Test
    void handlePracticeSessionAccessDeniedException_ShouldReturn403() {

        PracticeSessionAccessDeniedException exception =
                new PracticeSessionAccessDeniedException(
                        "You are not allowed to access this session."
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handlePracticeSessionAccessDeniedException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                403,
                "Forbidden",
                "You are not allowed to access this session."
        );
    }

    @Test
    void handleInvalidPracticeSessionStateException_ShouldReturn400() {

        InvalidPracticeSessionStateException exception =
                new InvalidPracticeSessionStateException(
                        "Practice session is already completed."
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleInvalidPracticeSessionStateException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                400,
                "Bad Request",
                "Practice session is already completed."
        );
    }

    @Test
    void handleValidationException_ShouldReturn400WithFieldErrors()
            throws NoSuchMethodException {

        TestRequest target = new TestRequest();

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        target,
                        "testRequest"
                );

        bindingResult.addError(
                new FieldError(
                        "testRequest",
                        "email",
                        "Email is required."
                )
        );

        MethodParameter methodParameter =
                new MethodParameter(
                        TestController.class.getDeclaredMethod(
                                "testMethod",
                                TestRequest.class
                        ),
                        0
                );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter,
                        bindingResult
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleValidationException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        ExceptionResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Validation failed", body.getMessage());
        assertEquals("/api/v1/test", body.getPath());
        assertNotNull(body.getTimestamp());

        assertNotNull(body.getValidationErrors());

        assertEquals(
                "Email is required.",
                body.getValidationErrors().get("email")
        );
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturn400() {

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Invalid JSON",
                        new RuntimeException("Malformed JSON"),
                        null
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleHttpMessageNotReadableException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertCommonResponse(
                response.getBody(),
                400,
                "Bad Request",
                "Invalid request body or unsupported value."
        );
    }

    @Test
    void handleUnexpectedException_ShouldReturnSafe500Response() {

        RuntimeException exception =
                new RuntimeException(
                        "Database password exposed internally"
                );

        ResponseEntity<ExceptionResponse> response =
                globalExceptionHandler
                        .handleUnexpectedException(
                                exception,
                                request
                        );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        ExceptionResponse body = response.getBody();

        assertNotNull(body);

        assertEquals(
                500,
                body.getStatus()
        );

        assertEquals(
                "Internal Server Error",
                body.getError()
        );

        assertEquals(
                "An unexpected error occurred. Please try again later.",
                body.getMessage()
        );

        assertEquals(
                "/api/v1/test",
                body.getPath()
        );

        assertNotNull(body.getTimestamp());

        assertNull(body.getValidationErrors());
    }

    private void assertCommonResponse(
            ExceptionResponse body,
            int expectedStatus,
            String expectedError,
            String expectedMessage) {

        assertNotNull(body);

        assertEquals(
                expectedStatus,
                body.getStatus()
        );

        assertEquals(
                expectedError,
                body.getError()
        );

        assertEquals(
                expectedMessage,
                body.getMessage()
        );

        assertEquals(
                "/api/v1/test",
                body.getPath()
        );

        assertNotNull(body.getTimestamp());
    }

    private static class TestRequest {

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    private static class TestController {

        @SuppressWarnings("unused")
        public void testMethod(TestRequest request) {
        }
    }
}