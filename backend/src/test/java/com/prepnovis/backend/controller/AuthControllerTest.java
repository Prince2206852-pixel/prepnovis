package com.prepnovis.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.prepnovis.backend.dto.request.ForgotPasswordRequest;
import com.prepnovis.backend.dto.request.ResetPasswordRequest;
import com.prepnovis.backend.dto.request.VerifyEmailRequest;
import com.prepnovis.backend.service.AuthService;
import com.prepnovis.backend.service.EmailVerificationService;
import com.prepnovis.backend.service.PasswordResetService;

class AuthControllerTest {

    private AuthService authService;
    private EmailVerificationService emailVerificationService;
    private PasswordResetService passwordResetService;
    private AuthController authController;

    @BeforeEach
    void setUp() {

        authService =
                mock(AuthService.class);

        emailVerificationService =
                mock(EmailVerificationService.class);

        passwordResetService =
                mock(PasswordResetService.class);

        authController = new AuthController(
                authService,
                emailVerificationService,
                passwordResetService
        );
    }

    @Test
    void verifyEmail_ShouldVerifyEmailSuccessfully() {

        VerifyEmailRequest request =
                new VerifyEmailRequest();

        request.setToken(
                "test-verification-token"
        );

        ResponseEntity<String> response =
                authController.verifyEmail(request);

        verify(emailVerificationService)
                .verifyEmail(
                        "test-verification-token"
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Email verified successfully.",
                response.getBody()
        );
    }

    @Test
    void forgotPassword_ShouldRequestPasswordResetSuccessfully() {

        ForgotPasswordRequest request =
                new ForgotPasswordRequest();

        request.setEmail(
                "prince@test.com"
        );

        ResponseEntity<String> response =
                authController.forgotPassword(request);

        verify(passwordResetService)
                .requestPasswordReset(
                        "prince@test.com"
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "If an eligible account exists for this email, a password reset link has been sent.",
                response.getBody()
        );
    }

    @Test
    void resetPassword_ShouldResetPasswordSuccessfully() {

        ResetPasswordRequest request =
                new ResetPasswordRequest();

        request.setToken(
                "test-password-reset-token"
        );

        request.setPassword(
                "NewPassword@123"
        );

        ResponseEntity<String> response =
                authController.resetPassword(request);

        verify(passwordResetService)
                .resetPassword(
                        "test-password-reset-token",
                        "NewPassword@123"
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Password reset successfully.",
                response.getBody()
        );
    }
}