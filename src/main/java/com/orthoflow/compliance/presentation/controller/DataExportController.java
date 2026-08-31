package com.orthoflow.compliance.presentation.controller;

import com.orthoflow.patient.domain.model.Patient;
import com.orthoflow.compliance.application.dto.PatientDataExport;
import com.orthoflow.compliance.application.service.DataExportService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * The engineering half of GDPR/Law 09-08 data-subject rights (audit V.8) —
 * export (Art. 15/20) and consent capture. Restricted to ADMIN: this
 * returns a patient's complete cross-module record in one response, which
 * is exactly the kind of bulk-access operation that should require the
 * same elevated trust as the hard-erasure path
 * ({@code PatientController.erasePatient}), not be reachable by any
 * authenticated staff account.
 *
 * What this endpoint does NOT cover: retention-period enforcement (no
 * statutory retention period is encoded here — that's a legal
 * determination for the operating clinic, not something to hardcode into
 * this codebase), a DPA/processor register, or CNDP notification filings.
 * Those are administrative/legal actions outside what a codebase can
 * discharge on its own — see AUDIT.md P3 item #43 and
 * docs/RUNBOOK.md.
 */
@RestController
@RequestMapping("/patients/{patientId}/compliance")
@RequiredArgsConstructor
public class DataExportController {

    private final DataExportService dataExportService;

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public PatientDataExport exportData(@PathVariable UUID patientId) {
        return dataExportService.exportPatientData(patientId);
    }

    public record ConsentRequest(@Size(max = 2000) String notes) {}

    @PostMapping("/consent")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient recordConsent(@PathVariable UUID patientId, @RequestBody(required = false) ConsentRequest request) {
        return dataExportService.recordConsent(patientId, request != null ? request.notes() : null);
    }
}
