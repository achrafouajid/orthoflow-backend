package com.orthoflow.scheduling.infrastructure.adapter.persistence;

import com.orthoflow.scheduling.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByPatientId(UUID patientId);
}
