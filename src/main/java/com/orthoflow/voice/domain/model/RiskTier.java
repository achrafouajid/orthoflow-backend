package com.orthoflow.voice.domain.model;

/**
 * How much ceremony a voice command needs before it is allowed to run
 * (audit XII.4 §3).
 */
public enum RiskTier {
    /** Navigation and reads. Executes on recognition; undoable by navigating back. */
    SAFE,
    /** Any clinical or financial write. Preview the resolved values, then explicit confirmation. */
    CONFIRM,
    /**
     * Deliberate physical act required — deleting a patient, voiding an
     * invoice, validating a stock count. Never reachable by voice at all; the
     * assistant acknowledges the request and points at the screen that does it.
     */
    BLOCKED
}
