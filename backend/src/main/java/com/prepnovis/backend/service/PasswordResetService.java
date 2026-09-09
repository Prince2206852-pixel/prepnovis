package com.prepnovis.backend.service;

public interface PasswordResetService {

    void requestPasswordReset(String email);

    void resetPassword(String rawToken, String newPassword);
}