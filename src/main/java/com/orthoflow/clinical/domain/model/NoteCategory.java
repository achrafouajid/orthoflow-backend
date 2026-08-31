package com.orthoflow.clinical.domain.model;

/**
 * Where a dictated note belongs. The voice layer resolves this from the
 * utterance where it can and asks the doctor where it cannot — guessing
 * between DENTAL_HISTORY and MEDICAL_HISTORY is exactly the kind of silent
 * assumption a clinical record must not make.
 */
public enum NoteCategory {
    GENERAL,
    CHIEF_COMPLAINT,
    OBSERVATION,
    DENTAL_HISTORY,
    MEDICAL_HISTORY,
    DIAGNOSIS,
    FOLLOW_UP,
    TREATMENT_PLAN
}
