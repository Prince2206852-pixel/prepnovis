package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.response.AnalyticsDashboardResponse;

public interface AnalyticsService {

    AnalyticsDashboardResponse getDashboard(String email);
}