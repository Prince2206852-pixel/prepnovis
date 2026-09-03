package com.prepnovis.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.response.AnalyticsDashboardResponse;
import com.prepnovis.backend.service.AnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Analytics",
        description = "APIs for viewing the authenticated user's practice performance and dashboard analytics."
)
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(
            summary = "Get analytics dashboard",
            description = "Returns practice statistics, scores, Saved Questions and PrepNovis Mock performance, and recent sessions for the authenticated user."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard(email);

        return ResponseEntity.ok(response);
    }
}