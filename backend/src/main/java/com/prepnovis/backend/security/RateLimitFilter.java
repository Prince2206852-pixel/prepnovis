package com.prepnovis.backend.security;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prepnovis.backend.dto.response.ExceptionResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LOGIN_LIMIT = 10;
    private static final int REGISTER_LIMIT = 5;

    private static final Duration WINDOW =
            Duration.ofMinutes(1);

    private final RateLimitService rateLimitService;

    private final JsonMapper jsonMapper =
            JsonMapper.builder().build();

    public RateLimitFilter(
            RateLimitService rateLimitService) {

        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        int limit;

        if ("/api/v1/auth/login".equals(path)) {
            limit = LOGIN_LIMIT;

        } else if ("/api/v1/auth/register".equals(path)) {
            limit = REGISTER_LIMIT;

        } else {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        String key =
                "rate-limit:"
                        + path
                        + ":"
                        + clientIp;

        boolean allowed =
                rateLimitService.isAllowed(
                        key,
                        limit,
                        WINDOW
                );

        if (!allowed) {
            writeTooManyRequestsResponse(
                    request,
                    response
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(
            HttpServletRequest request) {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }

    private void writeTooManyRequestsResponse(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        ExceptionResponse errorResponse =
                new ExceptionResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );
        errorResponse.setError(
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase()
        );
        errorResponse.setMessage(
                "Too many requests. Please try again later."
        );
        errorResponse.setPath(
                request.getRequestURI()
        );

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        jsonMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}