package com.orthoflow.voice.application.dto;

import lombok.Builder;

/**
 * The transcript of one captured clip, on its way back to the browser's voice
 * pipeline where it is fed through the exact same grammar → NLU → confirm path
 * a typed or browser-recognised utterance takes.
 *
 * <p>{@code error} is non-null when the server did not produce a transcript —
 * speech-to-text is switched off, or the provider was unreachable. The client
 * treats that as "fall back to the browser recogniser", not as a failed
 * utterance, so a misconfigured provider degrades rather than breaks.
 */
@Builder
public record TranscriptionResponse(
        String text,
        String provider,
        String model,
        /** Language Whisper detected for this clip, when it reported one. */
        String language,
        Double durationSeconds,
        String error
) {}
