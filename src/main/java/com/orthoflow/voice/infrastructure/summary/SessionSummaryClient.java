package com.orthoflow.voice.infrastructure.summary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Generates the consultation narrative from what the session actually
 * recorded, against any OpenAI-compatible chat endpoint.
 *
 * <p>Raw {@link HttpClient} rather than a vendor SDK, matching {@link
 * com.orthoflow.voice.infrastructure.nlu.OpenAiCompatibleNluProvider} — the
 * chat-completions shape is what every host implements, and not depending on
 * a vendor client keeps the provider swappable by configuration alone.
 *
 * <p>Returns null on any failure rather than throwing. A consultation whose
 * summary could not be generated is not a lost consultation: the findings are
 * in the audit trail and the review page renders them regardless, so the
 * doctor writes the observation themselves and nothing is blocked.
 */
@Component
@Slf4j
public class SessionSummaryClient {

    private final VoiceSummaryProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public SessionSummaryClient(VoiceSummaryProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build();
    }

    public boolean isConfigured() {
        return properties.getApiKey() != null && !properties.getApiKey().isBlank()
                && properties.getBaseUrl() != null && !properties.getBaseUrl().isBlank();
    }

    /**
     * @param systemPrompt the role and output contract
     * @param userPrompt   the session's recorded commands, rendered as text
     * @return the generated summary, or null if it could not be produced
     */
    public String summarise(String systemPrompt, String userPrompt) {
        if (!isConfigured()) {
            return null;
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", properties.getModel(),
                    "max_tokens", properties.getMaxOutputTokens(),
                    // A clinical summary should not vary between two runs on the
                    // same consultation — the doctor may regenerate it while
                    // editing, and a different narrative each time is alarming.
                    "temperature", 0,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)));

            String base = properties.getBaseUrl().replaceAll("/+$", "");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/chat/completions"))
                    .timeout(Duration.ofMillis(properties.getTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() / 100 != 2) {
                log.warn("Session summary provider returned HTTP {} — body: {}",
                        response.statusCode(), truncate(response.body()));
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("choices").path(0).path("message").path("content").asText("");
            return text.isBlank() ? null : text.trim();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            log.warn("Session summary call failed: {}", e.toString());
            return null;
        }
    }

    private static String truncate(String s) {
        if (s == null) return "";
        return s.length() <= 300 ? s : s.substring(0, 300) + "…";
    }
}
