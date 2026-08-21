package com.prepnovis.backend.service.impl;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.response.UserProfileResponse;
import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.exception.UserNotFoundException;
import com.prepnovis.backend.repository.UserRepository;
import com.prepnovis.backend.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(String email) {

        // Step 1: Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                   new UserNotFoundException("User not found.")
                 );

        // Step 2: Convert User entity to response DTO
        UserProfileResponse response = new UserProfileResponse();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}