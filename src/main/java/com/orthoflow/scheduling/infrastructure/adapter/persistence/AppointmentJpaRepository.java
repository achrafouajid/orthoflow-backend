package com.orthoflow.scheduling.infrastructure.adapter.persistence;

import com.orthoflow.scheduling.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientId(UUID patientId);

    /**
     * Loading the entire appointment history to render a single day's
     * schedule was the standout example in the audit's performance findings
     * (II.8/VI.4) — every screen filtered client-side after fetching every
     * appointment the clinic has ever had. No JOIN FETCH needed any more:
     * patient is a plain UUID column, not a relation, so there's no N+1 to
     * avoid — the batched PatientLookup#findSummaries call in
     * AppointmentService does the equivalent in one query per response set.
     */
    List<Appointment> findByDateTimeBetween(OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT a FROM Appointment a ORDER BY a.dateTime")
    List<Appointment> findAllOrderedByDateTime();
}
