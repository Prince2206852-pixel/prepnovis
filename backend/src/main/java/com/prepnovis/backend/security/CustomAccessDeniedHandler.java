package com.prepnovis.backend.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.prepnovis.backend.dto.response.ExceptionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        ExceptionResponse errorResponse = new ExceptionResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(
                "You do not have permission to access this resource."
        );
        errorResponse.setPath(request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        jsonMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}