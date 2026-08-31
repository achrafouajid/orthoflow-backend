package com.orthoflow.treatment.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.common.exception.NotFoundException;
import com.orthoflow.treatment.application.dto.PatientTreatmentRequest;
import com.orthoflow.treatment.application.dto.PatientTreatmentResponse;
import com.orthoflow.treatment.application.service.PatientTreatmentService;
import com.orthoflow.treatment.domain.model.PatientTreatment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientTreatmentController {

    private final PatientTreatmentService patientTreatmentService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/{patientId}/treatments")
    public ResponseEntity<List<PatientTreatmentResponse>> getTreatmentsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(patientTreatmentService.getTreatmentsByPatient(patientId).stream()
                .map(PatientTreatmentResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/treatments")
    public ResponseEntity<List<PatientTreatmentResponse>> getAllPatientTreatments() {
        return ResponseEntity.ok(patientTreatmentService.getAllPatientTreatments().stream()
                .map(PatientTreatmentResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<PatientTreatmentResponse> getTreatmentById(@PathVariable UUID patientId, @PathVariable UUID id) {
        return patientTreatmentService.getTreatmentById(id)
                .map(pt -> ResponseEntity.ok(PatientTreatmentResponse.from(pt)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{patientId}/treatments")
    public ResponseEntity<PatientTreatmentResponse> createPatientTreatment(
            @PathVariable UUID patientId, @Valid @RequestBody PatientTreatmentRequest request) {
        return ResponseEntity.ok(PatientTreatmentResponse.from(patientTreatmentService.createPatientTreatment(
                patientId, request, currentUserProvider.requireUserId())));
    }

    @PutMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<PatientTreatmentResponse> updatePatientTreatment(
            @PathVariable UUID patientId, @PathVariable UUID id, @Valid @RequestBody PatientTreatmentRequest request) {
        requireOwnedByPatient(patientId, id);
        return ResponseEntity.ok(PatientTreatmentResponse.from(patientTreatmentService.updatePatientTreatment(
                id, request, currentUserProvider.requireUserId())));
    }

    @DeleteMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<?> deletePatientTreatment(@PathVariable UUID patientId, @PathVariable UUID id) {
        requireOwnedByPatient(patientId, id);
        patientTreatmentService.deletePatientTreatment(id, currentUserProvider.requireUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Prevents an IDOR where a treatment id valid for one patient is submitted
     * under a different patient's URL — patientId was previously accepted
     * without ever being checked against the treatment's actual owner.
     */
    private void requireOwnedByPatient(UUID patientId, UUID treatmentId) {
        PatientTreatment treatment = patientTreatmentService.getTreatmentById(treatmentId)
                .orElseThrow(() -> new NotFoundException("Patient treatment not found"));
        if (treatment.getPatientId() == null || !patientId.equals(treatment.getPatientId())) {
            throw new NotFoundException("Patient treatment not found");
        }
    }
}
