package com.orthoflow.voice.infrastructure.stt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The two things unique to driving a general model as a transcriber: pulling
 * the transcript out of a step-shaped response, and noticing when the model
 * answered the audio instead of transcribing it.
 *
 * <p>The HTTP call itself is not exercised here — it is a single well-trodden
 * {@code HttpClient.send}, and a test that mocks it only asserts that Mockito
 * works. What is worth pinning down is the parsing, because Gemini's response
 * has no fixed path to the text and a wrong guess would put a thinking trace
 * into a clinical record.
 */
class GeminiTranscriptionClientTest {

    private GeminiTranscriptionClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        client = new GeminiTranscriptionClient(new SpeechToTextProperties(), objectMapper);
    }

    private String extractText(String json) throws Exception {
        Method method = GeminiTranscriptionClient.class
                .getDeclaredMethod("extractText", com.fasterxml.jackson.databind.JsonNode.class);
        method.setAccessible(true);
        return (String) method.invoke(null, objectMapper.readTree(json));
    }

    private boolean looksLikeRefusal(String text) throws Exception {
        Method method = GeminiTranscriptionClient.class.getDeclaredMethod("looksLikeRefusal", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, text);
    }

    private String mimeType(String contentType, String filename) throws Exception {
        Method method = GeminiTranscriptionClient.class
                .getDeclaredMethod("mimeType", String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, contentType, filename);
    }

    @Test
    void readsTheTranscriptOutOfTheModelOutputStep() throws Exception {
        String json = """
                {"steps":[{"type":"model_output","content":[
                  {"type":"text","text":"dent seize, carie récurrente"}]}]}
                """;
        assertThat(extractText(json)).isEqualTo("dent seize, carie récurrente");
    }

    @Test
    void skipsThinkingStepsRatherThanConcatenatingThemIntoTheTranscript() throws Exception {
        // A thought step carries the model's reasoning. Concatenated into the
        // transcript it would reach the grammar, and from there a clinical record.
        String json = """
                {"steps":[
                  {"type":"thought","signature":"EvEFCu4FAQw"},
                  {"type":"model_output","content":[{"type":"text","text":"dent dix-sept, couronne à refaire"}]}]}
                """;
        assertThat(extractText(json)).isEqualTo("dent dix-sept, couronne à refaire");
    }

    @Test
    void anEmptyOrUnexpectedResponseYieldsBlankRatherThanThrowing() throws Exception {
        assertThat(extractText("{}")).isEmpty();
        assertThat(extractText("{\"steps\":[]}")).isEmpty();
        assertThat(extractText("{\"steps\":[{\"type\":\"thought\"}]}")).isEmpty();
    }

    @Test
    void treatsAModelRefusalAsAFailedTranscriptionInBothLanguages() throws Exception {
        assertThat(looksLikeRefusal("I cannot help with that request.")).isTrue();
        assertThat(looksLikeRefusal("Je ne peux pas transcrire cet audio.")).isTrue();
        assertThat(looksLikeRefusal("As an AI, I don't have the ability to hear.")).isTrue();
    }

    @Test
    void doesNotMistakeOrdinarySpeechForARefusal() throws Exception {
        // A dentist saying this into the microphone is dictation, not a refusal.
        assertThat(looksLikeRefusal("le patient dit qu'il ne peut pas ouvrir la bouche complètement")).isFalse();
        assertThat(looksLikeRefusal("dent seize, carie récurrente")).isFalse();
        assertThat(looksLikeRefusal("")).isFalse();
    }

    @Test
    void aLongTranscriptIsNeverTreatedAsARefusal() throws Exception {
        // Refusals are short. A real transcript that happens to open with an
        // apology the patient made must not be discarded.
        String longUtterance = "I'm sorry " + "carie récurrente sur la seize, ".repeat(20);
        assertThat(longUtterance.length()).isGreaterThan(200);
        assertThat(looksLikeRefusal(longUtterance)).isFalse();
    }

    @Test
    void stripsCodecParametersMediaRecorderAddsToTheMimeType() throws Exception {
        // MediaRecorder reports audio/webm;codecs=opus, which the API rejects.
        assertThat(mimeType("audio/webm;codecs=opus", "audio.webm")).isEqualTo("audio/webm");
        assertThat(mimeType("audio/ogg; codecs=vorbis", "audio.ogg")).isEqualTo("audio/ogg");
    }

    @Test
    void fallsBackToTheFilenameThenToWebmForAnUnknownContentType() throws Exception {
        assertThat(mimeType("application/octet-stream", "clip.wav")).isEqualTo("audio/wav");
        assertThat(mimeType(null, "clip.m4a")).isEqualTo("audio/m4a");
        assertThat(mimeType(null, null)).isEqualTo("audio/webm");
    }

    @Test
    void isNotConfiguredWithoutAnApiKey() {
        SpeechToTextProperties properties = new SpeechToTextProperties();
        assertThat(new GeminiTranscriptionClient(properties, objectMapper).isConfigured()).isFalse();

        properties.setApiKey("test-key");
        assertThat(new GeminiTranscriptionClient(properties, objectMapper).isConfigured()).isTrue();
    }

    @Test
    void reportsAnUnconfiguredProviderInsteadOfAttemptingTheCall() {
        TranscriptionResult result =
                client.transcribe(new byte[] {1, 2, 3}, "audio.webm", "audio/webm", null, null);
        assertThat(result.error()).isEqualTo("stt-not-configured");
        assertThat(result.hasText()).isFalse();
    }
}
