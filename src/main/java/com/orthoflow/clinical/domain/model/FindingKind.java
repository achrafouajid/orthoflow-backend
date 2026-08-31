package com.orthoflow.clinical.domain.model;

/**
 * What a finding asserts about a tooth. The distinction matters clinically and
 * for the UI: "existing crown" and "crown replacement required" are two
 * separate facts that must coexist on the same tooth, not one overwriting the
 * other (the single-status model in {@link ToothState} could not express it).
 */
public enum FindingKind {
    /** A restoration or structure already present: crown, filling, implant… */
    EXISTING,
    /** Pathology or a clinical sign: caries, fracture, mobility, sensitivity… */
    CONDITION,
    /** Work the tooth needs: crown replacement, filling, extraction… */
    TREATMENT_REQUIRED,
    /** Watch-and-wait or a plain remark: monitor, normal, free-text note. */
    OBSERVATION
}
