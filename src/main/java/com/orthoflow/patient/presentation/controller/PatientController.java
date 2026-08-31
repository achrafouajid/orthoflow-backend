package com.orthoflow.patient.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.patient.domain.model.Patient;
import com.orthoflow.patient.application.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    /**
     * Defaults to 200/page instead of Spring's usual 20 — the dashboard and
     * dossier still compute their stats client-side over "all" patients, so
     * silently truncating to 20 would make completion rates and monthly
     * charts wrong for any clinic with more than 20 patients. 200 is a
     * pragmatic bound: enough for the app's actual current usage pattern,
     * while no longer being the fully unbounded findAll() of audit II.8.
     * Real cursor/page-based UI (item #32) can lower this once the frontend
     * pages explicitly instead of loading everything up front.
     */
    @GetMapping
    public Page<Patient> getAllPatients(
            @PageableDefault(size = 200, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String search) {
        return patientService.getAllPatients(pageable, search);
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient);
    }

    /**
     * Archives the patient — the everyday "delete" action. Does not touch
     * appointment or treatment history (audit II.15).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id, currentUserProvider.requireUserId());
    }

    /**
     * Permanently erases a patient and (via DB cascade) their appointments
     * and treatment history. A separate, ADMIN-only, deliberately harder to
     * reach path for a GDPR/Law 09-08 erasure request — never the default
     * "delete" action a doctor or assistant clicks day to day.
     */
    @DeleteMapping("/{id}/erase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void erasePatient(@PathVariable UUID id) {
        patientService.erasePatient(id);
    }
}
