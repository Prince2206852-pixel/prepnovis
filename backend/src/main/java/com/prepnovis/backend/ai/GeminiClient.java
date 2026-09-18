package com.prepnovis.backend.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Component
public class GeminiClient {

    private static final int MAX_ATTEMPTS = 3;

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.base-url}") String baseUrl) {

        this.apiKey = apiKey;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public String generateContent(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            try {

                Map response = restClient.post()
                        .uri("/models/{model}:generateContent", model)
                        .header("x-goog-api-key", apiKey)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

                return extractText(response);

            } catch (HttpServerErrorException ex) {

                if (!isServiceUnavailable(ex)
                        || attempt == MAX_ATTEMPTS) {

                    throw ex;
                }

                long delayMillis =
                        attempt == 1 ? 1000L : 2000L;

                System.out.println(
                        "Gemini temporarily unavailable. Retry "
                                + attempt
                                + "/"
                                + (MAX_ATTEMPTS - 1)
                                + " after "
                                + delayMillis
                                + " ms."
                );

                sleepBeforeRetry(delayMillis);
            }
        }

        throw new RuntimeException(
                "Gemini request failed after retries."
        );
    }

    private boolean isServiceUnavailable(
            HttpServerErrorException ex) {

        HttpStatusCode statusCode =
                ex.getStatusCode();

        return statusCode.value() == 503;
    }

    private void sleepBeforeRetry(
            long delayMillis) {

        try {

            Thread.sleep(delayMillis);

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Gemini retry was interrupted.",
                    ex
            );
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {

        if (response == null) {

            throw new RuntimeException(
                    "Empty response received from Gemini."
            );
        }

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>)
                        response.get("candidates");

        if (candidates == null
                || candidates.isEmpty()) {

            throw new RuntimeException(
                    "No response candidate received from Gemini."
            );
        }

        Map<String, Object> content =
                (Map<String, Object>)
                        candidates.get(0).get("content");

        if (content == null) {

            throw new RuntimeException(
                    "Gemini response does not contain content."
            );
        }

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>)
                        content.get("parts");

        if (parts == null
                || parts.isEmpty()) {

            throw new RuntimeException(
                    "Gemini response does not contain text parts."
            );
        }

        Object text =
                parts.get(0).get("text");

        if (!(text instanceof String)
                || ((String) text).isBlank()) {

            throw new RuntimeException(
                    "Gemini returned empty text."
            );
        }

        return (String) text;
    }
}