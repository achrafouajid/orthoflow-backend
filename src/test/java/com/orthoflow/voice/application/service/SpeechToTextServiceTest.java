package com.orthoflow.voice.application.service;

import com.orthoflow.common.exception.ValidationException;
import com.orthoflow.voice.application.dto.TranscriptionResponse;
import com.orthoflow.voice.infrastructure.stt.GroqTranscriptionClient;
import com.orthoflow.voice.infrastructure.stt.SpeechToTextProperties;
import com.orthoflow.voice.infrastructure.stt.TranscriptionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The two things this service must get right: a malformed upload is a hard
 * 400, but a switched-off or unreachable provider is a soft "fall back to the
 * browser recogniser" — never an exception, never a silently-empty success
 * that looks like the doctor said nothing.
 */
@ExtendWith(MockitoExtension.class)
class SpeechToTextServiceTest {

    @Mock
    private GroqTranscriptionClient client;

    private SpeechToTextProperties properties;
    private SpeechToTextService service;

    @BeforeEach
    void setUp() {
        properties = new SpeechToTextProperties();
        service = new SpeechToTextService(properties, client);
    }

    private MockMultipartFile clip(byte[] bytes) {
        return new MockMultipartFile("file", "audio.webm", "audio/webm", bytes);
    }

    @Test
    void rejectsAnEmptyUpload() {
        assertThatThrownBy(() -> service.transcribe(clip(new byte[0]), null, null))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(client);
    }

    @Test
    void rejectsAClipOverTheConfiguredCeiling() {
        properties.setMaxAudioBytes(8);
        assertThatThrownBy(() -> service.transcribe(clip(new byte[9]), null, null))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(client);
    }

    @Test
    void whenDisabledReturnsAnErrorTagAndNeverCallsTheProvider() {
        properties.setEnabled(false);

        TranscriptionResponse response = service.transcribe(clip(new byte[] {1, 2, 3}), null, null);

        assertThat(response.text()).isEmpty();
        assertThat(response.error()).isEqualTo("stt-disabled");
        verify(client, never()).transcribe(any(), any(), any(), any(), any());
    }

    @Test
    void whenEnabledMapsTheProviderTranscriptThrough() {
        properties.setEnabled(true);
        properties.setModel("whisper-large-v3-turbo");
        when(client.transcribe(any(), any(), any(), any(), any()))
                .thenReturn(TranscriptionResult.ofText("upper right first molar, recurrent caries", "french", 3.4));

        TranscriptionResponse response = service.transcribe(clip(new byte[] {1, 2, 3}), "fr", null);

        assertThat(response.text()).isEqualTo("upper right first molar, recurrent caries");
        assertThat(response.error()).isNull();
        assertThat(response.model()).isEqualTo("whisper-large-v3-turbo");
        assertThat(response.language()).isEqualTo("french");
        assertThat(response.durationSeconds()).isEqualTo(3.4);
    }

    @Test
    void anUpstreamFailureDegradesInsteadOfThrowing() {
        properties.setEnabled(true);
        when(client.transcribe(any(), any(), any(), any(), any()))
                .thenReturn(TranscriptionResult.ofError("stt-http-503"));

        TranscriptionResponse response = service.transcribe(clip(new byte[] {1, 2, 3}), null, null);

        assertThat(response.text()).isEmpty();
        assertThat(response.error()).isEqualTo("stt-http-503");
    }
}
