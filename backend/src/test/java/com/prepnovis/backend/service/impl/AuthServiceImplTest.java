package com.prepnovis.backend.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void register_ShouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode(request.getPassword()))
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
                "User registered successfully.",
                response.getMessage()
        );

        verify(userRepository)
                .existsByEmail("prince@test.com");

        verify(roleRepository)
                .findByName("USER");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
void register_ShouldThrowException_WhenEmailAlreadyExists() {

    when(userRepository.existsByEmail(request.getEmail()))
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
}
@Test
void login_ShouldLoginSuccessfully() {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("prince@test.com");
    loginRequest.setPassword("password123");

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setFullName("Prince Kumar");
    user.setEmail("prince@test.com");
    user.setPassword("encodedPassword");
    user.setRole(role);

    UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                    .withUsername("prince@test.com")
                    .password("encodedPassword")
                    .roles("USER")
                    .build();

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
    assertEquals("test-jwt-token", response.getAccessToken());

    verify(userRepository)
            .findByEmail("prince@test.com");

    verify(passwordEncoder)
            .matches("password123", "encodedPassword");

    verify(jwtService)
            .generateToken(userDetails);
}

@Test
void login_ShouldThrowException_WhenPasswordIsIncorrect() {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("prince@test.com");
    loginRequest.setPassword("wrongPassword");

    User user = new User();
    user.setEmail("prince@test.com");
    user.setPassword("encodedPassword");
    user.setRole(role);

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

    verify(passwordEncoder)
            .matches("wrongPassword", "encodedPassword");

    verify(jwtService, never())
            .generateToken(any(UserDetails.class));
}

}