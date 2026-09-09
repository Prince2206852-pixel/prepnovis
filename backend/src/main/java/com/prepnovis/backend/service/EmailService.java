package com.prepnovis.backend.service;

public interface EmailService {

    void sendEmailVerification(
            String recipientEmail,
            String recipientName,
            String verificationToken
    );

    void sendPasswordReset(
            String recipientEmail,
            String recipientName,
            String resetToken
    );
}