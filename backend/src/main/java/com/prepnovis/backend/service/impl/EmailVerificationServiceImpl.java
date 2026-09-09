package com.prepnovis.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prepnovis.backend.entity.EmailVerificationToken;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.repository.EmailVerificationTokenRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.EmailService;
import com.prepnovis.backend.service.EmailVerificationService;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int TOKEN_EXPIRY_HOURS = 24;

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationServiceImpl(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void createAndSendVerificationToken(User user) {

        if (user.isEmailVerified()) {
            return;
        }

        // Invalidate/remove any previous verification token for this user.
        tokenRepository.deleteAllByUser(user);

        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken =
                new EmailVerificationToken();

        verificationToken.setUser(user);
        verificationToken.setTokenHash(tokenHash);
        verificationToken.setExpiresAt(
                LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS)
        );

        tokenRepository.save(verificationToken);

        emailService.sendEmailVerification(
                user.getEmail(),
                user.getFullName(),
                rawToken
        );
    }

    @Override
    @Transactional
    public void verifyEmail(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Email verification token is required."
            );
        }

        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid email verification token."
                                )
                        );

        if (verificationToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "This email verification link has already been used."
            );
        }

        if (verificationToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "This email verification link has expired."
            );
        }

        User user = verificationToken.getUser();

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        verificationToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);
    }

    @Override
@Transactional
public void resendVerificationEmail(String email) {

    if (email == null || email.isBlank()) {
        return;
    }

    String normalizedEmail =
            email.trim().toLowerCase();

    User user = userRepository
            .findByEmail(normalizedEmail)
            .orElse(null);

    // Generic behavior prevents exposing whether
    // an account exists for this email.
    if (user == null) {
        return;
    }

    // Google accounts don't need PrepNovis email verification.
    if (user.getAuthProvider()
            != com.prepnovis.backend.entity.enums.AuthProvider.LOCAL) {
        return;
    }

    // No need to send another email if already verified.
    if (user.isEmailVerified()) {
        return;
    }

    createAndSendVerificationToken(user);
}

    private String generateSecureToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder result =
                new StringBuilder(bytes.length * 2);

        for (byte value : bytes) {
            result.append(
                    String.format("%02x", value & 0xff)
            );
        }

        return result.toString();
    }
}