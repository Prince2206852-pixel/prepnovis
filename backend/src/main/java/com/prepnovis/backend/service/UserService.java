package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(String email);
}