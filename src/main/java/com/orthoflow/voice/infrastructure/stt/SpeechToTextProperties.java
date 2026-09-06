package com.orthoflow.voice.infrastructure.stt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Server-side speech-to-text for the voice-first clinical workflow.
 *
 * <p>This is the sibling of {@link
 * com.orthoflow.voice.infrastructure.nlu.VoiceNluProperties} for the other end
 * of the pipeline: turning captured microphone audio into a transcript. The
 * browser's own {@code SpeechRecognition} still works and is still the default
 * on the client; this exists so a deployment can send audio to a hosted
 * Whisper endpoint instead of whatever cloud service the browser happens to
 * use (Chrome and Edge upload to Google; Safari is on-device).
 *
 * <p>{@code enabled: false} is the deliberate default. Turning it on means
 * consultation-room audio — which carries health information about the patient
 * and anyone else in the room — is sent to a third-party processor, so it
 * needs the same data-protection groundwork the NLU fallback does (DPA,
 * lawful basis, CNDP notification under Law 09-08). The API key is held here
 * on the server and never reaches the browser.
 *
 * <p>The default {@code base-url} targets Groq's OpenAI-compatible audio API;
 * any endpoint that implements {@code POST /audio/transcriptions} with the
 * same multipart shape (a self-hosted {@code faster-whisper} server, for
 * example) works by pointing {@code base-url} at it.
 */
@Component
@ConfigurationProperties(prefix = "orthoflow.voice.stt")
@Getter
@Setter
public class SpeechToTextProperties {

    /**
     * Whether {@code POST /voice/transcribe} will call the provider at all.
     * When false the endpoint answers 200 with an empty transcript and
     * {@code error: "stt-disabled"} so the client can fall back to
     * browser-side recognition without treating it as a failure.
     */
    private boolean enabled = false;

    /** {@code groq} is the only provider name recognised today. */
    private String provider = "groq";

    /** Held server-side only. Supplied via {@code VOICE_STT_API_KEY}. */
    private String apiKey = "";

    /**
     * Whisper variant. {@code whisper-large-v3-turbo} is fastest and cheapest
     * and is the right default for one-utterance clinical dictation;
     * {@code whisper-large-v3} is marginally more accurate on heavy accents.
     */
    private String model = "whisper-large-v3-turbo";

    /** OpenAI-compatible audio API root, no trailing slash. */
    private String baseUrl = "https://api.groq.com/openai/v1";

    /**
     * Optional ISO-639-1 hint (e.g. {@code fr}, {@code ar}, {@code en}). Left
     * blank, Whisper detects the language per utterance, which is the right
     * behaviour for Moroccan clinicians who code-switch mid-sentence.
     */
    private String language = "";

    /**
     * 0 keeps the decode deterministic — the correct choice for a clinical
     * record, where a reproducible transcript matters more than a fluent one.
     */
    private double temperature = 0;

    private int timeoutMs = 15000;

    /**
     * Hard ceiling on an uploaded clip, enforced before the provider call so
     * an oversized body is a fast, clear 400 rather than a slow upstream
     * rejection. 15 MB is far above any legitimate push-to-talk or
     * examination-segment recording.
     */
    private long maxAudioBytes = 15L * 1024 * 1024;
}
