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

    // Step 1: Check if email already exists
   if (userRepository.existsByEmail(request.getEmail())) {
    throw new EmailAlreadyExistsException(
            "Email '" + request.getEmail() + "' is already registered."
    );
}




    // Step 2: Fetch USER role
    Role role = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Default role USER not found."));

    // Step 3: Create User entity
    User user = new User();
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(role);

    // Step 4: Save User
    User savedUser = userRepository.save(user);

    // Step 5: Prepare Response
    RegisterUserResponse response = new RegisterUserResponse();
    response.setId(savedUser.getId());
    response.setFullName(savedUser.getFullName());
    response.setEmail(savedUser.getEmail());
    response.setRole(savedUser.getRole().getName());
    response.setMessage("User registered successfully.");

    return response;
}

@Override
public LoginResponse login(LoginRequest request) {

    // Step 1: Find user by email
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new InvalidCredentialsException("Invalid email or password.")
            );

    // Step 2: Check password
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new InvalidCredentialsException("Invalid email or password.");
    }

    // Step 3: Prepare response
    LoginResponse response = new LoginResponse();

    response.setId(user.getId());
    response.setFullName(user.getFullName());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole().getName());

    // JWT token will be added later
    UserDetails userDetails =
        customUserDetailsService.loadUserByUsername(user.getEmail());

String token = jwtService.generateToken(userDetails);

response.setAccessToken(token);

    return response;
}
}