package com.orthoflow.voice.application.service;

import com.orthoflow.common.exception.ValidationException;
import com.orthoflow.voice.application.dto.TranscriptionResponse;
import com.orthoflow.voice.infrastructure.stt.GroqTranscriptionClient;
import com.orthoflow.voice.infrastructure.stt.SpeechToTextProperties;
import com.orthoflow.voice.infrastructure.stt.TranscriptionResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Server-side capture-to-text. The browser records a clip and posts it here;
 * this hands back a transcript that re-enters the client pipeline exactly
 * where a browser-recognised utterance would.
 *
 * <p>Off by default. When {@code orthoflow.voice.stt.enabled} is false — or
 * the provider is unreachable — this returns a transcript-less {@link
 * TranscriptionResponse} carrying an {@code error} tag rather than throwing,
 * so the client falls back to its own recogniser. Only a malformed request
 * (no audio, or a clip past the configured ceiling) is a hard 400.
 */
@Service
@Slf4j
public class SpeechToTextService {

    private final SpeechToTextProperties properties;
    private final GroqTranscriptionClient client;

    public SpeechToTextService(SpeechToTextProperties properties, GroqTranscriptionClient client) {
        this.properties = properties;
        this.client = client;
    }

    @PostConstruct
    void reportConfiguration() {
        if (!properties.isEnabled()) {
            log.info("Server-side speech-to-text disabled. The browser's own SpeechRecognition is used; "
                    + "set orthoflow.voice.stt.enabled=true with an API key to route capture through "
                    + "a hosted Whisper endpoint instead.");
        } else if (!client.isConfigured()) {
            log.warn("orthoflow.voice.stt.enabled=true but no api-key/base-url is set — /voice/transcribe "
                    + "will report stt-not-configured and the client will fall back to browser recognition.");
        } else {
            log.info("Server-side speech-to-text enabled: provider={} model={} endpoint={}",
                    properties.getProvider(), properties.getModel(), properties.getBaseUrl());
        }
    }

    /**
     * @param audio    the recorded clip (webm/ogg/wav/mp3/m4a…)
     * @param language ISO-639-1 override, or null/blank to let Whisper detect
     * @param prompt   optional bias text (names, clinical terms), or null
     */
    public TranscriptionResponse transcribe(MultipartFile audio, String language, String prompt) {
        if (audio == null || audio.isEmpty()) {
            throw new ValidationException("No audio was received.");
        }
        if (audio.getSize() > properties.getMaxAudioBytes()) {
            throw new ValidationException("That recording is too large (limit "
                    + (properties.getMaxAudioBytes() / (1024 * 1024)) + " MB). Record a shorter clip.");
        }
        if (!properties.isEnabled()) {
            return disabled("stt-disabled");
        }

        byte[] bytes;
        try {
            bytes = audio.getBytes();
        } catch (IOException e) {
            throw new ValidationException("The uploaded recording could not be read.");
        }

        TranscriptionResult result = client.transcribe(
                bytes,
                filename(audio),
                audio.getContentType(),
                language,
                prompt);

        if (result.error() != null) {
            // Not an exception: the client shows the doctor an audible "that
            // didn't come through" and its own recogniser takes over.
            return disabled(result.error());
        }

        return TranscriptionResponse.builder()
                .text(result.text())
                .provider(properties.getProvider())
                .model(properties.getModel())
                .language(result.language())
                .durationSeconds(result.durationSeconds())
                .build();
    }

    private TranscriptionResponse disabled(String error) {
        return TranscriptionResponse.builder()
                .text("")
                .provider(properties.getProvider())
                .model(properties.getModel())
                .error(error)
                .build();
    }

    private static String filename(MultipartFile audio) {
        String original = audio.getOriginalFilename();
        if (original != null && !original.isBlank()) {
            return original;
        }
        String type = audio.getContentType();
        if (type != null && type.contains("ogg")) return "audio.ogg";
        if (type != null && type.contains("wav")) return "audio.wav";
        if (type != null && type.contains("mpeg")) return "audio.mp3";
        if (type != null && type.contains("mp4")) return "audio.mp4";
        return "audio.webm";
    }
}
