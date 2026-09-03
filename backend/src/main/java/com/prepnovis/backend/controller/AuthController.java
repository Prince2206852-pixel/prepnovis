package com.prepnovis.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;
import com.prepnovis.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Authentication",
        description = "APIs for user registration and login."
)
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new PrepNovis user account."
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {

        RegisterUserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user and returns a JWT access token."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}