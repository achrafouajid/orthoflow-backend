package com.orthoflow.voice.infrastructure.stt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Speech-to-text backed by any server that implements the OpenAI
 * {@code POST /audio/transcriptions} shape — Groq's hosted Whisper, or a
 * self-hosted {@code faster-whisper} / {@code whisper.cpp} server.
 *
 * <p>Raw {@link HttpClient} and a hand-built {@code multipart/form-data} body
 * rather than a vendor SDK, for the same reason {@link
 * com.orthoflow.voice.infrastructure.nlu.OpenAiCompatibleNluProvider} does it:
 * the multipart transcription shape is the one thing every Whisper host
 * implements, and not depending on a vendor client keeps the provider
 * swappable by configuration alone.
 *
 * <p>An upstream failure comes back as {@link TranscriptionResult#ofError} —
 * never a thrown exception — so the caller can fall back to browser-side
 * recognition instead of losing the utterance.
 */
@Component
@Slf4j
public class GroqTranscriptionClient implements TranscriptionProvider {

    private final SpeechToTextProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GroqTranscriptionClient(SpeechToTextProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build();
    }

    @Override
    public String name() {
        return "groq";
    }

    @Override
    public boolean isConfigured() {
        return properties.getApiKey() != null && !properties.getApiKey().isBlank()
                && properties.getBaseUrl() != null && !properties.getBaseUrl().isBlank();
    }

    /**
     * @param audio       raw bytes of the recorded clip
     * @param filename    original name — the extension (.webm/.ogg/.wav/.mp3…)
     *                    is how the provider infers the container
     * @param contentType MIME type reported by the browser's MediaRecorder
     * @param languageOverride ISO-639-1 hint, or null/blank to auto-detect
     * @param prompt      optional bias text (spelling of names, terms); may be null
     */
    @Override
    public TranscriptionResult transcribe(byte[] audio, String filename, String contentType,
                                          String languageOverride, String prompt) {
        if (!isConfigured()) {
            return TranscriptionResult.ofError("stt-not-configured");
        }
        try {
            String boundary = "----orthoflow" + Long.toHexString(System.nanoTime());
            List<byte[]> parts = new ArrayList<>();

            parts.add(field(boundary, "model", properties.getModel()));
            // verbose_json is what carries detected language and clip duration
            // back; the plain json format returns only the text.
            parts.add(field(boundary, "response_format", "verbose_json"));
            parts.add(field(boundary, "temperature", Double.toString(properties.getTemperature())));

            String language = languageOverride != null && !languageOverride.isBlank()
                    ? languageOverride.trim()
                    : properties.getLanguage();
            if (language != null && !language.isBlank()) {
                parts.add(field(boundary, "language", language.trim()));
            }
            if (prompt != null && !prompt.isBlank()) {
                parts.add(field(boundary, "prompt", prompt.trim()));
            }
            parts.add(filePart(boundary, "file", filename, contentType, audio));
            parts.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            byte[] body = concat(parts);
            String base = properties.getBaseUrl().replaceAll("/+$", "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/audio/transcriptions"))
                    .timeout(Duration.ofMillis(properties.getTimeoutMs()))
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() / 100 != 2) {
                log.warn("Speech-to-text provider returned HTTP {} — body: {}",
                        response.statusCode(), truncate(response.body()));
                return TranscriptionResult.ofError("stt-http-" + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("text").asText("");
            String detected = root.hasNonNull("language") ? root.get("language").asText() : null;
            Double duration = root.hasNonNull("duration") ? root.get("duration").asDouble() : null;
            return TranscriptionResult.ofText(text, detected, duration);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TranscriptionResult.ofError("stt-interrupted");
        } catch (Exception e) {
            log.warn("Speech-to-text call failed: {}", e.toString());
            return TranscriptionResult.ofError("stt-request-failed");
        }
    }

    // ── multipart/form-data assembly ────────────────────────────────────

    private static byte[] field(String boundary, String name, String value) {
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n";
        return header.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] filePart(String boundary, String name, String filename,
                                   String contentType, byte[] content) {
        String safeName = filename == null || filename.isBlank() ? "audio.webm" : filename;
        String mime = contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType;
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + safeName + "\"\r\n"
                + "Content-Type: " + mime + "\r\n\r\n";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(header.getBytes(StandardCharsets.UTF_8));
        out.writeBytes(content);
        out.writeBytes("\r\n".getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    private static byte[] concat(List<byte[]> parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            out.writeBytes(part);
        }
        return out.toByteArray();
    }

    private static String truncate(String s) {
        if (s == null) return "";
        return s.length() <= 300 ? s : s.substring(0, 300) + "…";
    }
}
