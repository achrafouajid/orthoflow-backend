package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.util.List;

/**
 * Everything the dossier needs to render the clinical picture in one round
 * trip — the dossier already makes four separate calls on load, and a voice
 * session that writes to three of these needs to refresh them together or the
 * doctor sees a half-updated screen.
 */
@Builder
public record PatientClinicalRecordResponse(
        List<ToothFindingResponse> findings,
        List<ClinicalNoteResponse> notes,
        List<AllergyResponse> allergies,
        List<MedicalHistoryResponse> medicalHistory
) {}
