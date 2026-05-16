package com.orthoflow.billing.application.service;

import com.orthoflow.billing.domain.model.Patient;
import com.orthoflow.billing.domain.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
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
        existing.setInsuranceProvider(updatedPatient.getInsuranceProvider());
        existing.setInsuranceNumber(updatedPatient.getInsuranceNumber());
        existing.setStatus(updatedPatient.getStatus());
        return patientRepository.save(existing);
    }

    @Transactional
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
