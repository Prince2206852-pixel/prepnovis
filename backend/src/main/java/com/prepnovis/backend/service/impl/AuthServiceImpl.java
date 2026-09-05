package com.prepnovis.backend.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;
import com.prepnovis.backend.entity.Role;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.exception.EmailAlreadyExistsException;
import com.prepnovis.backend.exception.InvalidCredentialsException;
import com.prepnovis.backend.repository.RoleRepository;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.security.CustomUserDetailsService;
import com.prepnovis.backend.security.JwtService;
import com.prepnovis.backend.service.AuthService;
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final CustomUserDetailsService customUserDetailsService;

    public AuthServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        CustomUserDetailsService customUserDetailsService) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
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
                    new RuntimeException("Default role USER not found.")
            );

    User user = new User();
    user.setFullName(request.getFullName().trim());
    user.setEmail(normalizedEmail);
    user.setPassword(
            passwordEncoder.encode(request.getPassword())
    );
    user.setRole(role);

    User savedUser = userRepository.save(user);

    RegisterUserResponse response =
            new RegisterUserResponse();

    response.setId(savedUser.getId());
    response.setFullName(savedUser.getFullName());
    response.setEmail(savedUser.getEmail());
    response.setRole(savedUser.getRole().getName());
    response.setMessage("User registered successfully.");

    return response;
}

@Override
public LoginResponse login(LoginRequest request) {

    // Step 1: Normalize email
    String normalizedEmail =
            request.getEmail().trim().toLowerCase();

    // Step 2: Find user by normalized email
    User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() ->
                    new InvalidCredentialsException(
                            "Invalid email or password."
                    )
            );

    // Step 3: Check password
    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())) {

        throw new InvalidCredentialsException(
                "Invalid email or password."
        );
    }

    // Step 4: Prepare response
    LoginResponse response = new LoginResponse();

    response.setId(user.getId());
    response.setFullName(user.getFullName());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole().getName());

    // Step 5: Generate JWT token
    UserDetails userDetails =
            customUserDetailsService.loadUserByUsername(
                    user.getEmail()
            );

    String token =
            jwtService.generateToken(userDetails);

    response.setAccessToken(token);

    return response;
}
}