package com.prepnovis.backend.exception;

public class PracticeSessionAccessDeniedException extends RuntimeException {

    public PracticeSessionAccessDeniedException(String message) {
        super(message);
    }
}