package com.orthoflow.scheduling.application.service;

import com.orthoflow.billing.domain.model.Patient;
import com.orthoflow.billing.domain.repository.PatientRepository;
import com.orthoflow.scheduling.application.dto.AppointmentRequest;
import com.orthoflow.scheduling.application.dto.AppointmentResponse;
import com.orthoflow.scheduling.domain.model.Appointment;
import com.orthoflow.scheduling.domain.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .dateTime(request.getDateTime())
                .type(request.getType())
                .status(request.getStatus())
                .notes(request.getNotes())
                .applianceStep(request.getApplianceStep())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(UUID id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (request.getPatientId() != null && !request.getPatientId().equals(appointment.getPatient().getId())) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            appointment.setPatient(patient);
        }

        if (request.getDateTime() != null) appointment.setDateTime(request.getDateTime());
        if (request.getType() != null) appointment.setType(request.getType());
        if (request.getStatus() != null) appointment.setStatus(request.getStatus());
        if (request.getNotes() != null) appointment.setNotes(request.getNotes());
        if (request.getApplianceStep() != null) appointment.setApplianceStep(request.getApplianceStep());

        Appointment updated = appointmentRepository.save(appointment);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                .dateTime(appointment.getDateTime())
                .type(appointment.getType())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .applianceStep(appointment.getApplianceStep())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
