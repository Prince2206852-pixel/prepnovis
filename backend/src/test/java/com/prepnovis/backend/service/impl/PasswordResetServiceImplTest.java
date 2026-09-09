package com.prepnovis.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.prepnovis.backend.entity.PasswordResetToken;
import com.prepnovis.backend.entity.Role;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.AuthProvider;
import com.prepnovis.backend.repository.PasswordResetTokenRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {

        Role role = new Role();
        role.setName("USER");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName("Prince Kumar");
        user.setEmail("prince@test.com");
        user.setPassword("old-encoded-password");
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(true);
        user.setRole(role);
    }

    @Test
    void requestPasswordReset_ShouldCreateTokenAndSendEmail_ForEligibleLocalUser() {

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);

        ArgumentCaptor<String> rawTokenCaptor =
                ArgumentCaptor.forClass(String.class);

        passwordResetService.requestPasswordReset(
                "  PRINCE@TEST.COM  "
        );

        verify(tokenRepository)
                .deleteAllByUser(user);

        verify(tokenRepository)
                .save(tokenCaptor.capture());

        PasswordResetToken savedToken =
                tokenCaptor.getValue();

        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getTokenHash());
        assertEquals(64, savedToken.getTokenHash().length());
        assertNotNull(savedToken.getExpiresAt());

        verify(emailService)
                .sendPasswordReset(
                        eq("prince@test.com"),
                        eq("Prince Kumar"),
                        rawTokenCaptor.capture()
                );

        String rawToken =
                rawTokenCaptor.getValue();

        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());

        // The raw token sent by email must never be stored directly.
        assertNotEquals(
                rawToken,
                savedToken.getTokenHash()
        );

        // Only SHA-256(raw token) is stored.
        assertEquals(
                sha256(rawToken),
                savedToken.getTokenHash()
        );

        LocalDateTime now =
                LocalDateTime.now();

        assertTrue(
                savedToken.getExpiresAt()
                        .isAfter(now.plusMinutes(29))
        );

        assertTrue(
                savedToken.getExpiresAt()
                        .isBefore(now.plusMinutes(31))
        );
    }

    @Test
    void requestPasswordReset_ShouldDoNothing_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset(
                "unknown@test.com"
        );

        verify(tokenRepository, never())
                .deleteAllByUser(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));

        verify(emailService, never())
                .sendPasswordReset(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void requestPasswordReset_ShouldDoNothing_ForGoogleAccount() {

        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setPassword(null);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset(
                "prince@test.com"
        );

        verify(tokenRepository, never())
                .deleteAllByUser(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));

        verify(emailService, never())
                .sendPasswordReset(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void requestPasswordReset_ShouldDoNothing_ForUnverifiedLocalAccount() {

        user.setEmailVerified(false);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset(
                "prince@test.com"
        );

        verify(tokenRepository, never())
                .deleteAllByUser(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));

        verify(emailService, never())
                .sendPasswordReset(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void requestPasswordReset_ShouldDoNothing_WhenLocalPasswordIsNull() {

        user.setPassword(null);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset(
                "prince@test.com"
        );

        verify(tokenRepository, never())
                .deleteAllByUser(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));

        verify(emailService, never())
                .sendPasswordReset(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void requestPasswordReset_ShouldDoNothing_WhenEmailIsBlank() {

        passwordResetService.requestPasswordReset(" ");

        verify(userRepository, never())
                .findByEmail(any());

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));

        verify(emailService, never())
                .sendPasswordReset(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void requestPasswordReset_ShouldInvalidateOldTokensBeforeCreatingNewToken() {

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset(
                "prince@test.com"
        );

        verify(tokenRepository)
                .deleteAllByUser(user);

        verify(tokenRepository)
                .save(any(PasswordResetToken.class));

        verify(emailService)
                .sendPasswordReset(
                        eq("prince@test.com"),
                        eq("Prince Kumar"),
                        any()
                );
    }

    @Test
    void resetPassword_ShouldUpdatePasswordAndMarkTokenUsed() {

        String rawToken =
                "valid-password-reset-token";

        PasswordResetToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusMinutes(20),
                        null
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        when(passwordEncoder.encode("NewPassword@123"))
                .thenReturn("new-encoded-password");

        passwordResetService.resetPassword(
                rawToken,
                "NewPassword@123"
        );

        assertEquals(
                "new-encoded-password",
                user.getPassword()
        );

        assertNotNull(token.getUsedAt());

        verify(passwordEncoder)
                .encode("NewPassword@123");

        verify(userRepository)
                .save(user);

        verify(tokenRepository)
                .save(token);
    }

    @Test
    void resetPassword_ShouldRejectBlankToken() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetService
                                .resetPassword(
                                        " ",
                                        "NewPassword@123"
                                )
                );

        assertEquals(
                "Invalid or expired password reset link.",
                exception.getMessage()
        );

        verify(tokenRepository, never())
                .findByTokenHash(any());

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void resetPassword_ShouldRejectInvalidToken() {

        String rawToken =
                "invalid-password-reset-token";

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetService
                                .resetPassword(
                                        rawToken,
                                        "NewPassword@123"
                                )
                );

        assertEquals(
                "Invalid or expired password reset link.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void resetPassword_ShouldRejectAlreadyUsedToken() {

        String rawToken =
                "already-used-reset-token";

        PasswordResetToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusMinutes(20),
                        LocalDateTime.now().minusMinutes(1)
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetService
                                .resetPassword(
                                        rawToken,
                                        "NewPassword@123"
                                )
                );

        assertEquals(
                "Invalid or expired password reset link.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_ShouldRejectExpiredToken() {

        String rawToken =
                "expired-password-reset-token";

        PasswordResetToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().minusMinutes(1),
                        null
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetService
                                .resetPassword(
                                        rawToken,
                                        "NewPassword@123"
                                )
                );

        assertEquals(
                "Invalid or expired password reset link.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_ShouldRejectTokenBelongingToGoogleAccount() {

        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setPassword(null);

        String rawToken =
                "google-account-reset-token";

        PasswordResetToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusMinutes(20),
                        null
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetService
                                .resetPassword(
                                        rawToken,
                                        "NewPassword@123"
                                )
                );

        assertEquals(
                "Invalid or expired password reset link.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    private PasswordResetToken createToken(
            String rawToken,
            LocalDateTime expiresAt,
            LocalDateTime usedAt) {

        PasswordResetToken token =
                new PasswordResetToken();

        token.setUser(user);
        token.setTokenHash(
                sha256(rawToken)
        );
        token.setExpiresAt(expiresAt);
        token.setUsedAt(usedAt);

        return token;
    }

    private String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {
                result.append(
                        String.format(
                                "%02x",
                                b & 0xff
                        )
                );
            }

            return result.toString();

        } catch (Exception exception) {

            throw new RuntimeException(exception);
        }
    }
}