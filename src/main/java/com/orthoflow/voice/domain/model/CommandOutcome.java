package com.orthoflow.voice.domain.model;

public enum CommandOutcome {
    EXECUTED,
    REJECTED,
    FAILED,
    /** Understood but under-specified — the assistant asked a question instead of acting. */
    CLARIFICATION,
    UNDONE
}
