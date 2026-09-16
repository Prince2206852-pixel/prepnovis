package com.prepnovis.backend.service.impl;

import org.springframework.stereotype.Service;

import com.prepnovis.backend.dto.request.FeedbackRequest;
import com.prepnovis.backend.service.EmailService;
import com.prepnovis.backend.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final EmailService emailService;

    public FeedbackServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendFeedback(FeedbackRequest request) {

        emailService.sendFeedback(
                request.getName().trim(),
                request.getEmail().trim(),
                request.getMessage().trim()
        );
    }
}