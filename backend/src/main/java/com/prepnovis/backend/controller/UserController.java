package com.prepnovis.backend.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.response.UserProfileResponse;
import com.prepnovis.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Users",
        description = "APIs for viewing authenticated user profile information."
)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current user profile",
            description = "Returns profile information for the currently authenticated user."
    )
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Principal principal) {

        String email = principal.getName();

        UserProfileResponse response =
                userService.getCurrentUserProfile(email);

        return ResponseEntity.ok(response);
    }
}