package com.orthoflow.patient.application.service;

import com.orthoflow.patient.domain.model.Patient;
import com.orthoflow.patient.domain.repository.PatientRepository;
import com.orthoflow.common.exception.ConflictException;
import com.orthoflow.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * A CIN matching an existing (non-archived) patient is almost always the
     * same person registered twice by mistake — the frontend previously had
     * no duplicate check at all (audit VIII.4). Phone is deliberately not
     * checked: guardians and family members legitimately share one number.
     * Email is skipped too: it's optional and the DB unique constraint
     * already rejects a real collision with a clear error.
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        if (patient.getCin() != null && !patient.getCin().isBlank()
                && patientRepository.existsByCin(patient.getCin())) {
            throw new ConflictException("A patient with CIN " + patient.getCin() + " already exists");
        }
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable, String search) {
        if (search == null || search.isBlank()) {
            return patientRepository.findAll(pageable);
        }
        return patientRepository.search(search.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    @Transactional
    public Patient updatePatient(UUID id, Patient updatedPatient) {
        Patient existing = getPatientById(id);
        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setDateOfBirth(updatedPatient.getDateOfBirth());
        existing.setGender(updatedPatient.getGender());
        existing.setEmail(updatedPatient.getEmail());
        existing.setPhone(updatedPatient.getPhone());
        existing.setAddress(updatedPatient.getAddress());
        existing.setCin(updatedPatient.getCin());
        existing.setGuardianName(updatedPatient.getGuardianName());
        existing.setGuardianPhone(updatedPatient.getGuardianPhone());
        existing.setInsuranceProvider(updatedPatient.getInsuranceProvider());
        existing.setInsuranceNumber(updatedPatient.getInsuranceNumber());
        existing.setStatus(updatedPatient.getStatus());
        return patientRepository.save(existing);
    }

    /**
     * Archives a patient (deleted_at/deleted_by) instead of deleting the
     * row. A real DELETE cascaded to appointments and treatment history with
     * no way to satisfy a statutory retention obligation afterwards (audit
     * II.15). See erasePatient for the genuinely destructive GDPR path.
     */
    @Transactional
    public void deletePatient(UUID id, UUID actorId) {
        Patient patient = getPatientById(id);
        patient.setDeletedAt(OffsetDateTime.now());
        patient.setDeletedBy(actorId);
        patientRepository.save(patient);
    }

    /**
     * Permanently removes a patient and (via DB cascade) their appointments
     * and treatment history. Restricted to ADMIN at the controller — this is
     * the dedicated, audited path for a GDPR/Law 09-08 erasure request, not
     * the everyday "delete patient" action.
     */
    @Transactional
    public void erasePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
