package com.orthoflow.scheduling.domain.repository;

import com.orthoflow.scheduling.domain.model.Appointment;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID id);
    List<Appointment> findAll();
    List<Appointment> findByPatientId(UUID patientId);
    List<Appointment> findByDateTimeBetween(OffsetDateTime start, OffsetDateTime end);
    void deleteById(UUID id);
}
