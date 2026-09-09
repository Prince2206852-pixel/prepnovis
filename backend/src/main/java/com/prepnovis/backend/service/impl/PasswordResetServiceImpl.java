package com.prepnovis.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prepnovis.backend.entity.PasswordResetToken;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.AuthProvider;
import com.prepnovis.backend.repository.PasswordResetTokenRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.EmailService;
import com.prepnovis.backend.service.PasswordResetService;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_EXPIRY_MINUTES = 30;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetServiceImpl(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {

        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        Optional<User> optionalUser =
                userRepository.findByEmail(normalizedEmail);

        // Keep the public behaviour generic so callers cannot
        // discover whether an account exists.
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        // Google users authenticate through Google and do not have
        // a PrepNovis LOCAL password to reset.
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            return;
        }

        // Unverified LOCAL accounts should complete email verification
        // rather than use password reset.
        if (!user.isEmailVerified()) {
            return;
        }

        if (user.getPassword() == null) {
            return;
        }

        // A newly requested reset invalidates any older reset links.
        passwordResetTokenRepository.deleteAllByUser(user);

        String rawToken = generateSecureToken();

        PasswordResetToken passwordResetToken =
                new PasswordResetToken();

        passwordResetToken.setUser(user);
        passwordResetToken.setTokenHash(hashToken(rawToken));
        passwordResetToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)
        );

        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendPasswordReset(
                user.getEmail(),
                user.getFullName(),
                rawToken
        );
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid or expired password reset link."
            );
        }

        String tokenHash = hashToken(rawToken);

        PasswordResetToken passwordResetToken =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired password reset link."
                                )
                        );

        if (passwordResetToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Invalid or expired password reset link."
            );
        }

        if (passwordResetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Invalid or expired password reset link."
            );
        }

        User user = passwordResetToken.getUser();

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException(
                    "Invalid or expired password reset link."
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        passwordResetToken.setUsedAt(LocalDateTime.now());

        passwordResetTokenRepository.save(passwordResetToken);
    }

    private String generateSecureToken() {

        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }
}