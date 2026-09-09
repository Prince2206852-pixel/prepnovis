package com.prepnovis.backend.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.prepnovis.backend.dto.request.GoogleLoginRequest;
import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;
import com.prepnovis.backend.entity.Role;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.AuthProvider;
import com.prepnovis.backend.exception.EmailAlreadyExistsException;
import com.prepnovis.backend.exception.InvalidCredentialsException;
import com.prepnovis.backend.repository.RoleRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.security.CustomUserDetailsService;
import com.prepnovis.backend.security.GoogleTokenVerifier;
import com.prepnovis.backend.security.JwtService;
import com.prepnovis.backend.service.EmailVerificationService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterUserRequest request;
    private Role role;

    @BeforeEach
    void setUp() {

        request = new RegisterUserRequest();
        request.setFullName("Prince Kumar");
        request.setEmail("prince@test.com");
        request.setPassword("password123");

        role = new Role();
        role.setName("USER");
    }

    @Test
    void register_ShouldRegisterUnverifiedLocalUserAndSendVerificationEmail() {

        when(userRepository.existsByEmail("prince@test.com"))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {

                    User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());

                    return user;
                });

        RegisterUserResponse response =
                authService.register(request);

        assertEquals("Prince Kumar", response.getFullName());
        assertEquals("prince@test.com", response.getEmail());
        assertEquals("USER", response.getRole());

        assertEquals(
                "Registration successful. Please check your email to verify your account.",
                response.getMessage()
        );

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                AuthProvider.LOCAL,
                savedUser.getAuthProvider()
        );

        assertEquals(
                "encodedPassword",
                savedUser.getPassword()
        );

        assertNull(savedUser.getProviderId());
        assertFalse(savedUser.isEmailVerified());

        verify(emailVerificationService)
                .createAndSendVerificationToken(savedUser);

        verify(userRepository)
                .existsByEmail("prince@test.com");

        verify(roleRepository)
                .findByName("USER");

        verify(passwordEncoder)
                .encode("password123");
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("prince@test.com"))
                .thenReturn(true);

        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Email 'prince@test.com' is already registered.",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("prince@test.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(emailVerificationService, never())
                .createAndSendVerificationToken(any(User.class));
    }

    @Test
    void login_ShouldLoginSuccessfully_WhenLocalUserIsVerified() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("prince@test.com");
        loginRequest.setPassword("password123");

        User user = createLocalUser(true);

        UserDetails userDetails =
                createUserDetails("prince@test.com");

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"))
                .thenReturn(true);

        when(customUserDetailsService
                .loadUserByUsername("prince@test.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-jwt-token");

        LoginResponse response =
                authService.login(loginRequest);

        assertEquals("Prince Kumar", response.getFullName());
        assertEquals("prince@test.com", response.getEmail());
        assertEquals("USER", response.getRole());

        assertEquals(
                "test-jwt-token",
                response.getAccessToken()
        );

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "encodedPassword"
                );

        verify(jwtService)
                .generateToken(userDetails);
    }

    @Test
    void login_ShouldRejectUnverifiedLocalUser_WhenPasswordIsCorrect() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("prince@test.com");
        loginRequest.setPassword("password123");

        User user = createLocalUser(false);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"))
                .thenReturn(true);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Please verify your email before signing in.",
                exception.getMessage()
        );

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "encodedPassword"
                );

        verify(customUserDetailsService, never())
                .loadUserByUsername(any());

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    @Test
    void login_ShouldNotRevealVerificationState_WhenPasswordIsIncorrect() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("prince@test.com");
        loginRequest.setPassword("wrongPassword");

        User user = createLocalUser(false);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"))
                .thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("prince@test.com");
        loginRequest.setPassword("wrongPassword");

        User user = createLocalUser(true);

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"))
                .thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    @Test
    void login_ShouldRejectPasswordLogin_ForGoogleUser() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("google@test.com");
        loginRequest.setPassword("anything");

        User user = createGoogleUser(
                "google@test.com",
                "google-sub-123"
        );

        when(userRepository.findByEmail("google@test.com"))
                .thenReturn(Optional.of(user));

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .matches(any(), any());

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    @Test
    void loginWithGoogle_ShouldLoginExistingGoogleUserSuccessfully() {

        GoogleLoginRequest googleRequest =
                createGoogleLoginRequest();

        GoogleIdToken.Payload payload =
                createGooglePayload(
                        "google-sub-123",
                        "google@test.com",
                        true,
                        "Google User"
                );

        User user = createGoogleUser(
                "google@test.com",
                "google-sub-123"
        );

        UserDetails userDetails =
                createUserDetails("google@test.com");

        when(googleTokenVerifier.verify("google-credential"))
                .thenReturn(payload);

        when(userRepository
                .findByAuthProviderAndProviderId(
                        AuthProvider.GOOGLE,
                        "google-sub-123"
                ))
                .thenReturn(Optional.of(user));

        when(customUserDetailsService
                .loadUserByUsername("google@test.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("google-jwt-token");

        LoginResponse response =
                authService.loginWithGoogle(googleRequest);

        assertEquals("Google User", response.getFullName());
        assertEquals("google@test.com", response.getEmail());
        assertEquals("USER", response.getRole());

        assertEquals(
                "google-jwt-token",
                response.getAccessToken()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginWithGoogle_ShouldCreateNewGoogleUserSuccessfully() {

        GoogleLoginRequest googleRequest =
                createGoogleLoginRequest();

        GoogleIdToken.Payload payload =
                createGooglePayload(
                        "google-sub-456",
                        " NEWUSER@TEST.COM ",
                        true,
                        "New Google User"
                );

        when(googleTokenVerifier.verify("google-credential"))
                .thenReturn(payload);

        when(userRepository
                .findByAuthProviderAndProviderId(
                        AuthProvider.GOOGLE,
                        "google-sub-456"
                ))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("newuser@test.com"))
                .thenReturn(Optional.empty());

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {

                    User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());

                    return user;
                });

        UserDetails userDetails =
                createUserDetails("newuser@test.com");

        when(customUserDetailsService
                .loadUserByUsername("newuser@test.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("new-google-jwt-token");

        LoginResponse response =
                authService.loginWithGoogle(googleRequest);

        assertEquals(
                "New Google User",
                response.getFullName()
        );

        assertEquals(
                "newuser@test.com",
                response.getEmail()
        );

        assertEquals("USER", response.getRole());

        assertEquals(
                "new-google-jwt-token",
                response.getAccessToken()
        );

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                AuthProvider.GOOGLE,
                savedUser.getAuthProvider()
        );

        assertEquals(
                "google-sub-456",
                savedUser.getProviderId()
        );

        assertEquals(
                "newuser@test.com",
                savedUser.getEmail()
        );

        assertEquals(
                "New Google User",
                savedUser.getFullName()
        );

        assertNull(savedUser.getPassword());
        assertEquals("USER", savedUser.getRole().getName());
        assertTrue(savedUser.isEmailVerified());

        verify(passwordEncoder, never())
                .encode(any());

        verify(emailVerificationService, never())
                .createAndSendVerificationToken(any(User.class));
    }

    @Test
    void loginWithGoogle_ShouldRejectInvalidGoogleCredential() {

        GoogleLoginRequest googleRequest =
                createGoogleLoginRequest();

        when(googleTokenVerifier.verify("google-credential"))
                .thenThrow(
                        new IllegalArgumentException(
                                "Invalid Google ID token."
                        )
                );

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.loginWithGoogle(
                                googleRequest
                        )
                );

        assertEquals(
                "Invalid Google credential.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginWithGoogle_ShouldRejectUnverifiedGoogleEmail() {

        GoogleLoginRequest googleRequest =
                createGoogleLoginRequest();

        GoogleIdToken.Payload payload =
                createGooglePayload(
                        "google-sub-123",
                        "google@test.com",
                        false,
                        "Google User"
                );

        when(googleTokenVerifier.verify("google-credential"))
                .thenReturn(payload);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.loginWithGoogle(
                                googleRequest
                        )
                );

        assertEquals(
                "Google account could not be verified.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginWithGoogle_ShouldReject_WhenEmailBelongsToLocalAccount() {

        GoogleLoginRequest googleRequest =
                createGoogleLoginRequest();

        GoogleIdToken.Payload payload =
                createGooglePayload(
                        "google-sub-999",
                        "prince@test.com",
                        true,
                        "Prince Kumar"
                );

        User localUser = createLocalUser(true);

        when(googleTokenVerifier.verify("google-credential"))
                .thenReturn(payload);

        when(userRepository
                .findByAuthProviderAndProviderId(
                        AuthProvider.GOOGLE,
                        "google-sub-999"
                ))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("prince@test.com"))
                .thenReturn(Optional.of(localUser));

        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> authService.loginWithGoogle(
                                googleRequest
                        )
                );

        assertEquals(
                "An account with this email already exists. Please sign in with your password.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    private User createLocalUser(boolean emailVerified) {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setFullName("Prince Kumar");
        user.setEmail("prince@test.com");
        user.setPassword("encodedPassword");
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setProviderId(null);
        user.setEmailVerified(emailVerified);
        user.setRole(role);

        return user;
    }

    private User createGoogleUser(
            String email,
            String providerId) {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setFullName("Google User");
        user.setEmail(email);
        user.setPassword(null);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        user.setEmailVerified(true);
        user.setRole(role);

        return user;
    }

    private GoogleLoginRequest createGoogleLoginRequest() {

        GoogleLoginRequest googleRequest =
                new GoogleLoginRequest();

        googleRequest.setCredential("google-credential");

        return googleRequest;
    }

    private GoogleIdToken.Payload createGooglePayload(
            String subject,
            String email,
            boolean emailVerified,
            String name) {

        GoogleIdToken.Payload payload =
                new GoogleIdToken.Payload();

        payload.setSubject(subject);
        payload.setEmail(email);
        payload.setEmailVerified(emailVerified);
        payload.set("name", name);

        return payload;
    }

    private UserDetails createUserDetails(String email) {

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("test-password")
                .roles("USER")
                .build();
    }
}