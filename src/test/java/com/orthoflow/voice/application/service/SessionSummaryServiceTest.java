package com.orthoflow.voice.application.service;

import com.orthoflow.voice.application.dto.SessionSummaryResponse;
import com.orthoflow.voice.application.dto.VoiceCommandAuditResponse;
import com.orthoflow.voice.infrastructure.summary.SessionSummaryClient;
import com.orthoflow.voice.infrastructure.summary.VoiceSummaryProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * What the model is allowed to see, and what it must never be asked to
 * summarise.
 *
 * <p>The consequential case is the filtering: a command the dentist rejected,
 * or one that failed to write, is in the audit trail but did not happen
 * clinically. Feeding those to the summariser produces a narrative describing
 * treatment that was declined — which the dentist then signs.
 */
@ExtendWith(MockitoExtension.class)
class SessionSummaryServiceTest {

    private static final UUID SESSION = UUID.randomUUID();

    @Mock
    private SessionSummaryClient client;

    @Mock
    private VoiceAuditService voiceAuditService;

    private VoiceSummaryProperties properties;
    private SessionSummaryService service;

    @BeforeEach
    void setUp() {
        properties = new VoiceSummaryProperties();
        properties.setEnabled(true);
        properties.setApiKey("test-key");
        lenient().when(client.isConfigured()).thenReturn(true);
        service = new SessionSummaryService(properties, client, voiceAuditService);
    }

    private VoiceCommandAuditResponse audit(String intent, String outcome, String confirmation) {
        return VoiceCommandAuditResponse.builder()
                .id(UUID.randomUUID())
                .sessionId(SESSION)
                .occurredAt(OffsetDateTime.now())
                .intent(intent)
                .entities("{\"fdi\":\"16\"}")
                .outcome(outcome)
                .confirmationStatus(confirmation)
                .build();
    }

    @Test
    void whenDisabledReportsSoWithoutCallingTheProvider() {
        properties.setEnabled(false);

        SessionSummaryResponse response = service.summarise(SESSION);

        assertThat(response.error()).isEqualTo("summary-disabled");
        assertThat(response.summary()).isEmpty();
        verify(client, never()).summarise(any(), any());
    }

    @Test
    void whenNothingWasRecordedReportsSoRatherThanAskingForASummaryOfNothing() {
        when(voiceAuditService.forSession(SESSION)).thenReturn(List.of());

        SessionSummaryResponse response = service.summarise(SESSION);

        assertThat(response.error()).isEqualTo("summary-nothing-recorded");
        verify(client, never()).summarise(any(), any());
    }

    @Test
    void excludesRejectedFailedAndUndoneCommandsFromWhatTheModelSees() {
        VoiceCommandAuditResponse undone = VoiceCommandAuditResponse.builder()
                .id(UUID.randomUUID())
                .sessionId(SESSION)
                .occurredAt(OffsetDateTime.now())
                .intent("clinical.undoneFinding")
                .outcome("EXECUTED")
                .confirmationStatus("CONFIRMED")
                .undoneAt(OffsetDateTime.now())
                .build();

        when(voiceAuditService.forSession(SESSION)).thenReturn(List.of(
                audit("clinical.keptFinding", "EXECUTED", "CONFIRMED"),
                audit("clinical.rejectedFinding", "REJECTED", "REJECTED"),
                audit("clinical.failedFinding", "FAILED", "CONFIRMED"),
                audit("clinical.pendingFinding", "CLARIFICATION", "PENDING"),
                undone));
        when(client.summarise(any(), any())).thenReturn("Résumé de la consultation");

        SessionSummaryResponse response = service.summarise(SESSION);

        ArgumentCaptor<String> userPrompt = ArgumentCaptor.forClass(String.class);
        verify(client).summarise(any(), userPrompt.capture());

        assertThat(userPrompt.getValue())
                .contains("clinical.keptFinding")
                // Still awaiting the dentist's confirmation, but it is part of
                // what the consultation recorded.
                .contains("clinical.pendingFinding")
                .doesNotContain("clinical.rejectedFinding")
                .doesNotContain("clinical.failedFinding")
                .doesNotContain("clinical.undoneFinding");
        assertThat(response.summary()).isEqualTo("Résumé de la consultation");
        assertThat(response.commandCount()).isEqualTo(2);
        assertThat(response.error()).isNull();
    }

    @Test
    void asksForTheSummaryInTheConfiguredLanguage() {
        when(voiceAuditService.forSession(SESSION))
                .thenReturn(List.of(audit("clinical.addFindings", "EXECUTED", "CONFIRMED")));
        when(client.summarise(any(), any())).thenReturn("Résumé");

        service.summarise(SESSION);

        ArgumentCaptor<String> systemPrompt = ArgumentCaptor.forClass(String.class);
        verify(client).summarise(systemPrompt.capture(), any());
        assertThat(systemPrompt.getValue()).contains("French");
    }

    @Test
    void truncatesALongSessionAndSaysSoRatherThanTimingOut() {
        properties.setMaxCommands(2);
        when(voiceAuditService.forSession(SESSION)).thenReturn(List.of(
                audit("clinical.first", "EXECUTED", "CONFIRMED"),
                audit("clinical.second", "EXECUTED", "CONFIRMED"),
                audit("clinical.third", "EXECUTED", "CONFIRMED")));
        when(client.summarise(any(), any())).thenReturn("Résumé partiel");

        SessionSummaryResponse response = service.summarise(SESSION);

        ArgumentCaptor<String> userPrompt = ArgumentCaptor.forClass(String.class);
        verify(client).summarise(any(), userPrompt.capture());

        assertThat(response.truncated()).isTrue();
        assertThat(response.commandCount()).isEqualTo(2);
        // The most recent survive, and the model is told the view is partial.
        assertThat(userPrompt.getValue())
                .contains("only the most recent")
                .contains("clinical.third")
                .doesNotContain("clinical.first");
    }

    @Test
    void anUpstreamFailureReportsAnErrorRatherThanAnEmptySummaryThatLooksReal() {
        when(voiceAuditService.forSession(SESSION))
                .thenReturn(List.of(audit("clinical.addFindings", "EXECUTED", "CONFIRMED")));
        when(client.summarise(any(), any())).thenReturn(null);

        SessionSummaryResponse response = service.summarise(SESSION);

        assertThat(response.error()).isEqualTo("summary-request-failed");
        assertThat(response.summary()).isEmpty();
    }
}
