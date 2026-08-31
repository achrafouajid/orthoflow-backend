package com.orthoflow.compliance.application.dto;

import com.orthoflow.billing.domain.model.Invoice;
import com.orthoflow.patient.domain.model.Patient;
import com.orthoflow.scheduling.domain.model.Appointment;
import com.orthoflow.treatment.domain.model.PatientTreatment;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The full export of one patient's data across every module that holds a
 * record of them — the GDPR Art. 15 (right of access) / Art. 20 (data
 * portability) response, and the shape a data-subject erasure or rectification
 * request is checked against before acting. There was previously no way to
 * produce this at all (audit V.8): fulfilling a real data-subject request
 * meant manually querying four different tables.
 *
 * Deliberately a plain read-side aggregate assembled by
 * {@code DataExportService}, not a persisted entity — it exists only to be
 * serialized to the requester, never stored.
 */
@Getter
@Builder
public class PatientDataExport {
    private OffsetDateTime exportedAt;
    private Patient patient;
    private List<Appointment> appointments;
    private List<PatientTreatment> treatments;
    private List<Invoice> invoices;
}
