package com.prepnovis.backend.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.prepnovis.backend.dto.response.ExceptionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        ExceptionResponse errorResponse = new ExceptionResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorResponse.setMessage(
                "Authentication is required to access this resource."
        );
        errorResponse.setPath(request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        jsonMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}