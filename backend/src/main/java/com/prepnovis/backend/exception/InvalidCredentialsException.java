package com.prepnovis.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.prepnovis.backend.dto.response.ExceptionResponse;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(
        InvalidCredentialsException ex) {

    ExceptionResponse response = new ExceptionResponse();
    response.setTimestamp(LocalDateTime.now());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    response.setMessage(ex.getMessage());

    return new ResponseEntity<>(
            response,
            HttpStatus.UNAUTHORIZED
    );
}

}