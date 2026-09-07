package com.orthoflow.voice.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * What the doctor decided at review, and the only way a buffered examination
 * reaches the clinical record.
 *
 * <p>Commands are identified by audit id rather than resent as values. The
 * server already holds what it recorded and showed; re-deriving the write from
 * its own row is what keeps "what executes" identical to "what was previewed",
 * and it means this payload cannot smuggle in a finding the doctor never saw.
 *
 * <p>Editing at review is therefore not an update to an existing row. An
 * amendment rejects the original and records a fresh {@code manual} command in
 * its place, so the trail says plainly that the dentist changed what the
 * system heard — which is more honest, and more useful in a dispute, than a
 * mutated row that reads as though the system got it right first time.
 */
@Getter
@Setter
public class CommitVoiceSessionRequest {

    /** Pending audit ids to execute, in the order given. */
    @NotNull
    private List<UUID> approvedAuditIds = List.of();

    /** Pending audit ids the doctor removed at review. */
    @NotNull
    private List<UUID> rejectedAuditIds = List.of();

    /** Corrections made at review. */
    @NotNull
    private List<Amendment> amendments = List.of();

    /**
     * The narrative as the doctor edited it, frozen onto the session. Stored
     * verbatim — this is the text they signed off, not a regeneration.
     */
    private String summary;

    @Getter
    @Setter
    public static class Amendment {
        /** The command this replaces; rejected as part of the same commit. */
        @NotNull
        private UUID originalAuditId;

        /** Intent id for the corrected command. */
        @NotNull
        private String intent;

        /** Resolved entities as a JSON object, same shape the audit row holds. */
        @NotNull
        private String entities;
    }
}
