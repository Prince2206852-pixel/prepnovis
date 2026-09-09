package com.prepnovis.backend.service;

import com.prepnovis.backend.entity.User;

public interface EmailVerificationService {

    void createAndSendVerificationToken(User user);

    void verifyEmail(String rawToken);

    void resendVerificationEmail(String email);
}