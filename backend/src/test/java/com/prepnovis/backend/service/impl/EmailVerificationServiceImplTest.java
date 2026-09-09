package com.prepnovis.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prepnovis.backend.entity.EmailVerificationToken;
import com.prepnovis.backend.entity.Role;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.repository.EmailVerificationTokenRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private User user;

    @BeforeEach
    void setUp() {

        Role role = new Role();
        role.setName("USER");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName("Prince Kumar");
        user.setEmail("prince@test.com");
        user.setEmailVerified(false);
        user.setRole(role);
    }

    @Test
    void createAndSendVerificationToken_ShouldCreateTokenAndSendEmail() {

        ArgumentCaptor<EmailVerificationToken> tokenCaptor =
                ArgumentCaptor.forClass(EmailVerificationToken.class);

        ArgumentCaptor<String> rawTokenCaptor =
                ArgumentCaptor.forClass(String.class);

        emailVerificationService
                .createAndSendVerificationToken(user);

        verify(tokenRepository)
                .deleteAllByUser(user);

        verify(tokenRepository)
                .save(tokenCaptor.capture());

        EmailVerificationToken savedToken =
                tokenCaptor.getValue();

        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getTokenHash());
        assertEquals(64, savedToken.getTokenHash().length());
        assertNotNull(savedToken.getExpiresAt());

        verify(emailService)
                .sendEmailVerification(
                        org.mockito.ArgumentMatchers.eq(
                                "prince@test.com"
                        ),
                        org.mockito.ArgumentMatchers.eq(
                                "Prince Kumar"
                        ),
                        rawTokenCaptor.capture()
                );

        String rawToken =
                rawTokenCaptor.getValue();

        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());

        assertEquals(
                sha256(rawToken),
                savedToken.getTokenHash()
        );

        assertTrue(
                savedToken.getExpiresAt()
                        .isAfter(LocalDateTime.now())
        );
    }

    @Test
    void createAndSendVerificationToken_ShouldDoNothing_WhenAlreadyVerified() {

        user.setEmailVerified(true);

        emailVerificationService
                .createAndSendVerificationToken(user);

        verify(tokenRepository, never())
                .deleteAllByUser(any(User.class));

        verify(tokenRepository, never())
                .save(any(EmailVerificationToken.class));

        verify(emailService, never())
                .sendEmailVerification(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void verifyEmail_ShouldVerifyUserSuccessfully() {

        String rawToken =
                "test-verification-token";

        EmailVerificationToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusHours(1),
                        null
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        emailVerificationService
                .verifyEmail(rawToken);

        assertTrue(user.isEmailVerified());
        assertNotNull(token.getUsedAt());

        verify(userRepository)
                .save(user);

        verify(tokenRepository)
                .save(token);
    }

    @Test
    void verifyEmail_ShouldMarkTokenUsed_WhenUserAlreadyVerified() {

        user.setEmailVerified(true);

        String rawToken =
                "already-verified-token";

        EmailVerificationToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusHours(1),
                        null
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        emailVerificationService
                .verifyEmail(rawToken);

        assertNotNull(token.getUsedAt());

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository)
                .save(token);
    }

    @Test
    void verifyEmail_ShouldRejectBlankToken() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> emailVerificationService
                                .verifyEmail(" ")
                );

        assertEquals(
                "Email verification token is required.",
                exception.getMessage()
        );

        verify(tokenRepository, never())
                .findByTokenHash(any());
    }

    @Test
    void verifyEmail_ShouldRejectInvalidToken() {

        String rawToken =
                "invalid-token";

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> emailVerificationService
                                .verifyEmail(rawToken)
                );

        assertEquals(
                "Invalid email verification token.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository, never())
                .save(any(EmailVerificationToken.class));
    }

    @Test
    void verifyEmail_ShouldRejectAlreadyUsedToken() {

        String rawToken =
                "used-token";

        EmailVerificationToken token =
                createToken(
                        rawToken,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().minusMinutes(5)
                );

        when(tokenRepository.findByTokenHash(
                sha256(rawToken)
        )).thenReturn(Optional.of(token));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> emailVerificationService
                                .verifyEmail(rawToken)
                );

        assertEquals(
                "This email verification link has already been used.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void verifyEmail_ShouldRejectExpiredToken() {

        String rawToken =
                "expired-token";

        EmailVerificationToken token =
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
                        () -> emailVerificationService
                                .verifyEmail(rawToken)
                );

        assertEquals(
                "This email verification link has expired.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(tokenRepository, never())
                .save(any(EmailVerificationToken.class));
    }

    private EmailVerificationToken createToken(
            String rawToken,
            LocalDateTime expiresAt,
            LocalDateTime usedAt) {

        EmailVerificationToken token =
                new EmailVerificationToken();

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