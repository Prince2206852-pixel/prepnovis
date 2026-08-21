package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.request.LoginRequest;
import com.prepnovis.backend.dto.request.RegisterUserRequest;
import com.prepnovis.backend.dto.response.LoginResponse;
import com.prepnovis.backend.dto.response.RegisterUserResponse;

public interface AuthService {

    RegisterUserResponse register(RegisterUserRequest request);

    LoginResponse login(LoginRequest request);
}