package com.prepnovis.backend.service;

import com.prepnovis.backend.dto.request.FeedbackRequest;

public interface FeedbackService {

    void sendFeedback(FeedbackRequest request);
}