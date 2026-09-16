package com.prepnovis.backend.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.prepnovis.backend.service.EmailService;

@Service
public class ResendEmailService implements EmailService {

    private final RestClient restClient;
    private final String fromEmail;
    private final String frontendUrl;
    private final String feedbackRecipientEmail;

    public ResendEmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${app.feedback-email}") String feedbackRecipientEmail) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();

        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
        this.feedbackRecipientEmail = feedbackRecipientEmail;
    }

    @Override
    public void sendEmailVerification(
            String recipientEmail,
            String recipientName,
            String verificationToken) {

        String verificationUrl =
                frontendUrl
                        + "/verify-email?token="
                        + verificationToken;

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>Verify your PrepNovis email</h2>

                    <p>Hi %s,</p>

                    <p>
                        Thanks for creating your PrepNovis account.
                        Please verify your email address to activate your account.
                    </p>

                    <p style="margin: 30px 0;">
                        <a href="%s"
                           style="
                               background: #4f46e5;
                               color: white;
                               padding: 12px 20px;
                               text-decoration: none;
                               border-radius: 6px;
                               display: inline-block;">
                            Verify Email
                        </a>
                    </p>

                    <p>
                        If you didn't create a PrepNovis account,
                        you can safely ignore this email.
                    </p>

                    <p>PrepNovis</p>
                </div>
                """.formatted(
                escapeHtml(recipientName),
                verificationUrl
        );

        Map<String, Object> requestBody = Map.of(
                "from", "PrepNovis <" + fromEmail + ">",
                "to", List.of(recipientEmail),
                "subject", "Verify your PrepNovis email",
                "html", html
        );

        sendEmail(requestBody);
    }

    @Override
    public void sendPasswordReset(
            String recipientEmail,
            String recipientName,
            String resetToken) {

        String resetUrl =
                frontendUrl
                        + "/reset-password?token="
                        + resetToken;

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2>Reset your PrepNovis password</h2>

                    <p>Hi %s,</p>

                    <p>
                        We received a request to reset your PrepNovis password.
                        Click the button below to create a new password.
                    </p>

                    <p style="margin: 30px 0;">
                        <a href="%s"
                           style="
                               background: #4f46e5;
                               color: white;
                               padding: 12px 20px;
                               text-decoration: none;
                               border-radius: 6px;
                               display: inline-block;">
                            Reset Password
                        </a>
                    </p>

                    <p>
                        If you didn't request a password reset,
                        you can safely ignore this email.
                    </p>

                    <p>PrepNovis</p>
                </div>
                """.formatted(
                escapeHtml(recipientName),
                resetUrl
        );

        Map<String, Object> requestBody = Map.of(
                "from", "PrepNovis <" + fromEmail + ">",
                "to", List.of(recipientEmail),
                "subject", "Reset your PrepNovis password",
                "html", html
        );

        sendEmail(requestBody);
    }

    @Override
    public void sendFeedback(
            String senderName,
            String senderEmail,
            String message) {

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 650px; margin: auto;">
                    <h2>New PrepNovis Feedback</h2>

                    <p>
                        A new feedback message was submitted from the PrepNovis website.
                    </p>

                    <div style="
                        margin: 24px 0;
                        padding: 18px;
                        background: #f8fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                    ">
                        <p><strong>Name:</strong> %s</p>
                        <p><strong>Email:</strong> %s</p>

                        <p style="margin-top: 20px;">
                            <strong>Feedback:</strong>
                        </p>

                        <p style="white-space: pre-wrap; line-height: 1.6;">
                            %s
                        </p>
                    </div>

                    <p>PrepNovis Feedback System</p>
                </div>
                """.formatted(
                escapeHtml(senderName),
                escapeHtml(senderEmail),
                escapeHtml(message)
        );

        Map<String, Object> requestBody = Map.of(
                "from", "PrepNovis <" + fromEmail + ">",
                "to", List.of(feedbackRecipientEmail),
                "reply_to", senderEmail,
                "subject", "New PrepNovis Feedback from " + senderName,
                "html", html
        );

        sendEmail(requestBody);
    }

    private void sendEmail(Map<String, Object> requestBody) {

        restClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}