package com.orthoflow.stock.domain.model;

import com.orthoflow.billing.domain.model.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patient_treatments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientTreatment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(nullable = false)
    private String teeth; // Comma separated tooth IDs e.g. "11,12"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PatientTreatmentStatus status = PatientTreatmentStatus.PLANNED;

    @Column(nullable = false)
    @Builder.Default
    private int progress = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "stock_movements_generated", nullable = false)
    @Builder.Default
    private boolean stockMovementsGenerated = false;

    @OneToMany(mappedBy = "patientTreatment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PatientTreatmentConsumable> consumables = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public void addConsumable(PatientTreatmentConsumable consumable) {
        consumables.add(consumable);
        consumable.setPatientTreatment(this);
    }
}
