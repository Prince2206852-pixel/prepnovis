package com.prepnovis.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prepnovis.backend.dto.response.ExceptionResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {

        ExceptionResponse response = new ExceptionResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setError(HttpStatus.CONFLICT.getReasonPhrase());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ExceptionResponse> handleValidationException(
        MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ExceptionResponse response = new ExceptionResponse();
    response.setTimestamp(LocalDateTime.now());
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    response.setMessage("Validation failed");
    response.setValidationErrors(errors);

    return ResponseEntity.badRequest().body(response);
}

@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<ExceptionResponse> handleUserNotFoundException(
        UserNotFoundException ex) {

    ExceptionResponse response = new ExceptionResponse();
    response.setTimestamp(LocalDateTime.now());
    response.setStatus(HttpStatus.NOT_FOUND.value());
    response.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
    response.setMessage(ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(QuestionNotFoundException.class)
public ResponseEntity<ExceptionResponse> handleQuestionNotFoundException(
        QuestionNotFoundException ex) {

    ExceptionResponse response = new ExceptionResponse();
    response.setTimestamp(LocalDateTime.now());
    response.setStatus(HttpStatus.NOT_FOUND.value());
    response.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
    response.setMessage(ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
}

}