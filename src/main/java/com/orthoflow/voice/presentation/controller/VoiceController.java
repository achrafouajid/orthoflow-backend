package com.orthoflow.voice.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.clinical.application.dto.PatientClinicalRecordResponse;
import com.orthoflow.clinical.application.service.ClinicalRecordService;
import com.orthoflow.clinical.domain.model.FindingCatalog;
import com.orthoflow.voice.application.dto.*;
import com.orthoflow.voice.application.service.VoiceAuditService;
import com.orthoflow.voice.application.service.VoiceCommandService;
import com.orthoflow.voice.application.service.VoiceSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The voice pipeline's server side: session lifecycle, the audit trail, and
 * the confirm/reject gate on any clinical write a dictated command produced.
 *
 * <p>Every write here is DOCTOR/ADMIN only, same as {@link
 * com.orthoflow.clinical.presentation.controller.ClinicalRecordController} —
 * a voice command inherits its permission from the JWT it runs under, not
 * from a separate voice-specific permission model. Reads (the lexicon, the
 * audit trail) are open to any authenticated user so a read-only assistant
 * account can still see what a session recorded.
 */
@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceSessionService voiceSessionService;
    private final VoiceCommandService voiceCommandService;
    private final ClinicalRecordService clinicalRecordService;
    private final VoiceAuditService voiceAuditService;
    private final CurrentUserProvider currentUserProvider;

    // ── Sessions ─────────────────────────────────────────────────────────

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public VoiceSessionResponse startSession(@RequestBody(required = false) StartVoiceSessionRequest request) {
        return voiceSessionService.start(request != null ? request : new StartVoiceSessionRequest(),
                currentUserProvider.requireUserId());
    }

    @PostMapping("/sessions/{sessionId}/end")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public VoiceSessionResponse endSession(
            @PathVariable UUID sessionId,
            @RequestBody(required = false) CompleteVoiceSessionRequest request) {
        return voiceSessionService.end(sessionId, request != null ? request : new CompleteVoiceSessionRequest(),
                currentUserProvider.requireUserId());
    }

    @GetMapping("/sessions/{sessionId}")
    public VoiceSessionResponse getSession(@PathVariable UUID sessionId) {
        return voiceSessionService.get(sessionId);
    }

    @GetMapping("/sessions")
    public List<VoiceSessionResponse> listSessions(@RequestParam UUID patientId) {
        return voiceSessionService.listByPatient(patientId);
    }

    /**
     * What this dictated examination actually persisted, read back from the
     * clinical tables rather than from anything the browser accumulated.
     *
     * That distinction is the point: the doctor signs off on the record as
     * stored, so a write that silently failed cannot appear in the summary
     * they confirm.
     */
    @GetMapping("/sessions/{sessionId}/summary")
    public PatientClinicalRecordResponse sessionSummary(@PathVariable UUID sessionId) {
        return clinicalRecordService.getSessionRecord(sessionId);
    }

    // ── Commands ────────────────────────────────────────────────────────

    /**
     * Logs one interpreted command. For a SAFE command this is called after
     * the client has already acted on it (navigation/read); for a CONFIRM
     * command it must be called with {@code confirmationStatus = PENDING} and
     * nothing has executed yet — see {@link #confirm}.
     */
    @PostMapping("/commands")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public VoiceCommandAuditResponse recordCommand(@Valid @RequestBody RecordVoiceCommandRequest request) {
        return voiceCommandService.record(request, currentUserProvider.requireUserId());
    }

    @PostMapping("/commands/{auditId}/confirm")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public VoiceCommandAuditResponse confirmCommand(@PathVariable UUID auditId) {
        return voiceCommandService.confirm(auditId, currentUserProvider.requireUserId());
    }

    @PostMapping("/commands/{auditId}/reject")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public VoiceCommandAuditResponse rejectCommand(@PathVariable UUID auditId) {
        return voiceCommandService.reject(auditId, currentUserProvider.requireUserId());
    }

    /** The trail for one patient, or for one session — pass exactly one. */
    @GetMapping("/commands")
    public List<VoiceCommandAuditResponse> listCommands(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID sessionId) {
        if (sessionId != null) return voiceAuditService.forSession(sessionId);
        if (patientId != null) return voiceAuditService.forPatient(patientId);
        throw new IllegalArgumentException("Provide patientId or sessionId");
    }

    // ── NLU fallback ────────────────────────────────────────────────────

    @PostMapping("/interpret")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public InterpretResponse interpret(@Valid @RequestBody InterpretRequest request) {
        return voiceCommandService.interpret(request);
    }

    // ── Lexicon ─────────────────────────────────────────────────────────

    /**
     * Publishes {@link FindingCatalog} so the browser's grammar can assert at
     * startup that its spoken-language synonyms map onto codes this server
     * actually accepts, per {@code FindingCatalog}'s own javadoc, which names
     * this exact path. (An equivalent read already exists at
     * {@code GET /clinical/finding-catalog} — kept as-is; this is the path the
     * domain model documents and the voice lexicon fetches from.)
     */
    @GetMapping("/lexicon")
    public Map<String, Object> lexicon() {
        List<Map<String, Object>> entries = FindingCatalog.all().values().stream()
                .map(def -> Map.<String, Object>of(
                        "code", def.code(),
                        "kind", def.kind().name(),
                        "impliedStatus", def.impliedStatus() == null ? "" : def.impliedStatus(),
                        "statusPriority", def.statusPriority()))
                .sorted((a, b) -> ((String) a.get("code")).compareTo((String) b.get("code")))
                .toList();
        return Map.of("codes", FindingCatalog.codes().stream().sorted().toList(), "definitions", entries);
    }
}
