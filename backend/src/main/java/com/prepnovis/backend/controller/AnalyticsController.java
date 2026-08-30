package com.prepnovis.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepnovis.backend.dto.response.AnalyticsDashboardResponse;
import com.prepnovis.backend.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        AnalyticsDashboardResponse response =
                analyticsService.getDashboard(email);

        return ResponseEntity.ok(response);
    }
}