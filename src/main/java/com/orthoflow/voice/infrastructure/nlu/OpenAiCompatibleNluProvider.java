package com.orthoflow.voice.infrastructure.nlu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orthoflow.voice.application.dto.InterpretRequest;
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
 * Natural-language fallback backed by any server speaking the OpenAI
 * chat-completions shape — Ollama, vLLM, LM Studio, llama.cpp's server.
 *
 * <p>This exists so a clinic can answer the data-protection question by
 * running the model itself. Pointed at {@code http://localhost:11434/v1} on
 * the same VPS as the database, no utterance leaves the machine, and the
 * disclosure analysis that a cloud provider would require does not arise.
 *
 * <p>Raw HTTP rather than a vendor SDK is deliberate: the point of this
 * provider is that it is not tied to a vendor, and the chat-completions shape
 * is the one thing every local runtime implements.
 */
@Component
@Slf4j
public class OpenAiCompatibleNluProvider implements NluProvider {

    private final VoiceNluProperties properties;
    private final NluPromptBuilder promptBuilder;
    private final NluResponseParser responseParser;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCompatibleNluProvider(VoiceNluProperties properties,
                                       NluPromptBuilder promptBuilder,
                                       NluResponseParser responseParser,
                                       ObjectMapper objectMapper) {
        this.properties = properties;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getNlu().getTimeoutMs()))
                .build();
    }

    @Override
    public String name() {
        return "openai-compatible";
    }

    @Override
    public boolean isAvailable() {
        String baseUrl = properties.getNlu().getBaseUrl();
        return baseUrl != null && !baseUrl.isBlank();
    }

    @Override
    public NluInterpretation interpret(InterpretRequest request) {
        if (!isAvailable()) {
            return NluInterpretation.unavailable(name(),
                    "openai-compatible NLU selected but orthoflow.voice.nlu.base-url is not set");
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", properties.getNlu().getModel(),
                    "max_tokens", properties.getNlu().getMaxOutputTokens(),
                    "temperature", 0,
                    // Honoured by Ollama and vLLM; harmlessly ignored elsewhere,
                    // which is why the parser still tolerates fenced output.
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", promptBuilder.systemPrompt(request)),
                            Map.of("role", "user", "content", promptBuilder.userPrompt(request))));

            String base = properties.getNlu().getBaseUrl().replaceAll("/+$", "");
            HttpRequest.Builder httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/chat/completions"))
                    .timeout(Duration.ofMillis(properties.getNlu().getTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));

            String apiKey = properties.getNlu().getApiKey();
            if (apiKey != null && !apiKey.isBlank()) {
                httpRequest.header("Authorization", "Bearer " + apiKey);
            }

            HttpResponse<String> response =
                    httpClient.send(httpRequest.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() / 100 != 2) {
                log.warn("openai-compatible NLU returned HTTP {}", response.statusCode());
                return NluInterpretation.unavailable(name(), "NLU HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("choices").path(0).path("message").path("content").asText("");
            return responseParser.parse(text, request, name());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return NluInterpretation.unavailable(name(), "NLU request interrupted");
        } catch (Exception e) {
            log.warn("openai-compatible NLU call failed: {}", e.toString());
            return NluInterpretation.unavailable(name(), "NLU request failed: " + e.getClass().getSimpleName());
        }
    }
}
