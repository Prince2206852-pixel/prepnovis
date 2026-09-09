package com.prepnovis.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.request.ForgotPasswordRequest;
import com.prepnovis.backend.dto.request.GoogleLoginRequest;
import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.request.ResendVerificationRequest;
import com.prepnovis.backend.dto.request.ResetPasswordRequest;
import com.prepnovis.backend.dto.request.VerifyEmailRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;
import com.prepnovis.backend.service.AuthService;
import com.prepnovis.backend.service.EmailVerificationService;
import com.prepnovis.backend.service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Authentication",
        description = "APIs for user registration, login, email verification and password reset."
)
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService) {

        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
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

    @Operation(
            summary = "Login with Google",
            description = "Verifies a Google ID token and authenticates or registers the PrepNovis user."
    )
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request) {

        LoginResponse response =
                authService.loginWithGoogle(request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Verify email",
            description = "Verifies the email address of a PrepNovis local account."
    )
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        emailVerificationService.verifyEmail(
                request.getToken()
        );

        return ResponseEntity.ok(
                "Email verified successfully."
        );
    }

    @Operation(
            summary = "Resend verification email",
            description = "Sends a new verification email when an unverified local account exists."
    )
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationRequest request) {

        emailVerificationService.resendVerificationEmail(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "If an unverified account exists for this email, a verification email has been sent."
        );
    }

    @Operation(
            summary = "Forgot password",
            description = "Sends a password reset email when an eligible local account exists."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.requestPasswordReset(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "If an eligible account exists for this email, a password reset link has been sent."
        );
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the password using a valid one-time password reset token."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                "Password reset successfully."
        );
    }
}