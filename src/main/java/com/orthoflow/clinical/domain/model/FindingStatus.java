package com.orthoflow.clinical.domain.model;

/**
 * Findings are never hard-deleted — a clinical record that can silently lose
 * an entry is not auditable. RETRACTED is for "the system misheard me";
 * RESOLVED is for "this was true and has since been treated".
 */
public enum FindingStatus {
    ACTIVE,
    RESOLVED,
    RETRACTED
}
