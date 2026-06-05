package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.PatientTreatmentService;
import com.orthoflow.stock.domain.model.PatientTreatment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class PatientTreatmentController {

    private final PatientTreatmentService patientTreatmentService;

    @GetMapping("/{patientId}/treatments")
    public ResponseEntity<List<PatientTreatment>> getTreatmentsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(patientTreatmentService.getTreatmentsByPatient(patientId));
    }

    @GetMapping("/treatments")
    public ResponseEntity<List<PatientTreatment>> getAllPatientTreatments() {
        return ResponseEntity.ok(patientTreatmentService.getAllPatientTreatments());
    }

    @GetMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<PatientTreatment> getTreatmentById(@PathVariable UUID patientId, @PathVariable UUID id) {
        return patientTreatmentService.getTreatmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{patientId}/treatments")
    public ResponseEntity<PatientTreatment> createPatientTreatment(@PathVariable UUID patientId, @RequestBody PatientTreatment pt) {
        // Ensure the correct patient object is initialized
        if (pt.getPatient() == null) {
            pt.setPatient(com.orthoflow.billing.domain.model.Patient.builder().id(patientId).build());
        } else {
            pt.getPatient().setId(patientId);
        }
        return ResponseEntity.ok(patientTreatmentService.createPatientTreatment(pt));
    }

    @PutMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<PatientTreatment> updatePatientTreatment(@PathVariable UUID patientId, @PathVariable UUID id, @RequestBody PatientTreatment pt) {
        return ResponseEntity.ok(patientTreatmentService.updatePatientTreatment(id, pt));
    }

    @DeleteMapping("/{patientId}/treatments/{id}")
    public ResponseEntity<?> deletePatientTreatment(@PathVariable UUID patientId, @PathVariable UUID id) {
        patientTreatmentService.deletePatientTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
