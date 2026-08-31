package com.orthoflow.voice.domain.model;

public enum ConfirmationStatus {
    /** Risk tier SAFE — ran without asking. */
    AUTO,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    PENDING
}
