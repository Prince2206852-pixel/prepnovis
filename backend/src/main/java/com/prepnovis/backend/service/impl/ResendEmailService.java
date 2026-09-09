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

    public ResendEmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail,
            @Value("${app.frontend-url}") String frontendUrl) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();

        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
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

        restClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
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