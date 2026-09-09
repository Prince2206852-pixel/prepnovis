package com.prepnovis.backend.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.prepnovis.backend.service.AuthService;
import com.prepnovis.backend.service.EmailVerificationService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final EmailVerificationService emailVerificationService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService,
            GoogleTokenVerifier googleTokenVerifier,
            EmailVerificationService emailVerificationService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.googleTokenVerifier = googleTokenVerifier;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public RegisterUserResponse register(RegisterUserRequest request) {

        String normalizedEmail =
                request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "Email '" + normalizedEmail + "' is already registered."
            );
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException(
                                "Default role USER not found."
                        )
                );

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setProviderId(null);
        user.setEmailVerified(false);
        user.setRole(role);

        User savedUser = userRepository.save(user);

        emailVerificationService
                .createAndSendVerificationToken(savedUser);

        RegisterUserResponse response =
                new RegisterUserResponse();

        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().getName());
        response.setMessage(
                "Registration successful. Please check your email to verify your account."
        );

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        String normalizedEmail =
                request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        )
                );

        if (user.getAuthProvider() != AuthProvider.LOCAL
                || user.getPassword() == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        if (!user.isEmailVerified()) {
            throw new InvalidCredentialsException(
                    "Please verify your email before signing in."
            );
        }

        return createLoginResponse(user);
    }

    @Override
    public LoginResponse loginWithGoogle(
            GoogleLoginRequest request) {

        GoogleIdToken.Payload payload;

        try {

            payload = googleTokenVerifier.verify(
                    request.getCredential()
            );

        } catch (IllegalArgumentException exception) {

            throw new InvalidCredentialsException(
                    "Invalid Google credential."
            );
        }

        String providerId = payload.getSubject();
        String email = payload.getEmail();
        Boolean emailVerified = payload.getEmailVerified();

        if (providerId == null
                || providerId.isBlank()
                || email == null
                || email.isBlank()
                || !Boolean.TRUE.equals(emailVerified)) {

            throw new InvalidCredentialsException(
                    "Google account could not be verified."
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        User googleUser = userRepository
                .findByAuthProviderAndProviderId(
                        AuthProvider.GOOGLE,
                        providerId
                )
                .orElse(null);

        if (googleUser != null) {
            return createLoginResponse(googleUser);
        }

        User existingUser =
                userRepository.findByEmail(normalizedEmail)
                        .orElse(null);

        if (existingUser != null) {

            if (existingUser.getAuthProvider()
                    == AuthProvider.LOCAL) {

                throw new EmailAlreadyExistsException(
                        "An account with this email already exists. Please sign in with your password."
                );
            }

            throw new InvalidCredentialsException(
                    "Google account does not match the existing account."
            );
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException(
                                "Default role USER not found."
                        )
                );

        String fullName = extractGoogleFullName(
                payload,
                normalizedEmail
        );

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(normalizedEmail);
        user.setPassword(null);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        user.setEmailVerified(true);
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return createLoginResponse(savedUser);
    }

    private LoginResponse createLoginResponse(User user) {

        LoginResponse response =
                new LoginResponse();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        response.setAccessToken(token);

        return response;
    }

    private String extractGoogleFullName(
            GoogleIdToken.Payload payload,
            String email) {

        Object nameClaim = payload.get("name");

        if (nameClaim != null) {

            String name =
                    nameClaim.toString().trim();

            if (!name.isBlank()) {
                return name;
            }
        }

        int atIndex = email.indexOf("@");

        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }

        return "PrepNovis User";
    }
}