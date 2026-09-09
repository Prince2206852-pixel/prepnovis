package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;
import com.prepnovis.backend.dto.request.GoogleLoginRequest;

public interface AuthService {

    RegisterUserResponse register(RegisterUserRequest request);

    LoginResponse login(LoginRequest request);
    LoginResponse loginWithGoogle(GoogleLoginRequest request);
}