package com.orthoflow.voice.domain.model;

public enum VoiceSessionStatus {
    /** Dictation in progress; commands are being audited but nothing is written. */
    ACTIVE,

    /**
     * Dictation finished, the clinical record not yet written.
     *
     * <p>The state the buffered workflow lives in: the dentist has said "end
     * session", the summary has been generated, and they are reviewing and
     * correcting before saving. Without it, a consultation that was dictated
     * but never saved is indistinguishable from one that was — and the first
     * thing a clinic needs is a list of examinations still awaiting review,
     * because that is where the work that a closed browser tab interrupted
     * shows up.
     */
    PENDING_REVIEW,

    /** Reviewed, saved, and written to the clinical record. */
    COMPLETED,

    ABANDONED
}
