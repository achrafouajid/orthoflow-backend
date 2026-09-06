package com.orthoflow.voice.infrastructure.stt;

/**
 * What the provider made of one audio clip.
 *
 * <p>A blank {@code text} with a non-null {@code error} is an expected result,
 * not an exception: the provider was unreachable, or misconfigured, or
 * declined the clip. The client falls back to browser-side recognition in
 * that case rather than failing the utterance outright, mirroring how the
 * NLU fallback treats an unavailable provider.
 */
public record TranscriptionResult(
        String text,
        String language,
        Double durationSeconds,
        String error
) {
    public static TranscriptionResult ofText(String text, String language, Double durationSeconds) {
        return new TranscriptionResult(text == null ? "" : text.trim(), language, durationSeconds, null);
    }

    public static TranscriptionResult ofError(String error) {
        return new TranscriptionResult("", null, null, error);
    }

    public boolean hasText() {
        return text != null && !text.isBlank();
    }
}
