package com.orthoflow.stock.application.service;

import com.orthoflow.billing.domain.model.Patient;
import com.orthoflow.billing.domain.repository.PatientRepository;
import com.orthoflow.stock.domain.model.*;
import com.orthoflow.stock.domain.repository.PatientTreatmentRepository;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import com.orthoflow.stock.domain.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientTreatmentService {

    private final PatientTreatmentRepository patientTreatmentRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final StockItemRepository stockItemRepository;
    private final StockService stockService;

    public List<PatientTreatment> getTreatmentsByPatient(UUID patientId) {
        return patientTreatmentRepository.findByPatientId(patientId);
    }

    public List<PatientTreatment> getAllPatientTreatments() {
        return patientTreatmentRepository.findAll();
    }

    public Optional<PatientTreatment> getTreatmentById(UUID id) {
        return patientTreatmentRepository.findById(id);
    }

    @Transactional
    public PatientTreatment createPatientTreatment(PatientTreatment pt) {
        if (pt.getId() == null) {
            pt.setId(UUID.randomUUID());
        }

        Patient patient = patientRepository.findById(pt.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + pt.getPatient().getId()));
        pt.setPatient(patient);

        Treatment treatment = treatmentRepository.findById(pt.getTreatment().getId())
                .orElseThrow(() -> new IllegalArgumentException("Treatment not found: " + pt.getTreatment().getId()));
        pt.setTreatment(treatment);

        // Populate default consumables if not already specified
        if (pt.getConsumables() == null || pt.getConsumables().isEmpty()) {
            List<PatientTreatmentConsumable> consumables = new ArrayList<>();
            if (treatment.getConsumables() != null) {
                for (TreatmentConsumable tc : treatment.getConsumables()) {
                    PatientTreatmentConsumable ptc = PatientTreatmentConsumable.builder()
                            .patientTreatment(pt)
                            .stockItem(tc.getStockItem())
                            .quantityUsed(tc.getQuantityUsed())
                            .pricePerUnit(tc.getStockItem().getPricePerUse())
                            .notes(tc.getNotes())
                            .build();
                    consumables.add(ptc);
                }
            }
            pt.setConsumables(consumables);
        } else {
            for (PatientTreatmentConsumable ptc : pt.getConsumables()) {
                ptc.setPatientTreatment(pt);
                if (ptc.getStockItem() != null && ptc.getStockItem().getId() != null) {
                    StockItem stockItem = stockItemRepository.findById(ptc.getStockItem().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + ptc.getStockItem().getId()));
                    ptc.setStockItem(stockItem);
                }
            }
        }

        if (pt.getStatus() == PatientTreatmentStatus.COMPLETED) {
            deductStock(pt);
        }

        return patientTreatmentRepository.save(pt);
    }

    @Transactional
    public PatientTreatment updatePatientTreatment(UUID id, PatientTreatment updated) {
        PatientTreatment existing = patientTreatmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient Treatment not found: " + id));

        existing.setTeeth(updated.getTeeth());
        existing.setProgress(updated.getProgress());
        existing.setNotes(updated.getNotes());
        existing.setDoctorName(updated.getDoctorName());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());

        // Update consumables list
        existing.getConsumables().clear();
        if (updated.getConsumables() != null) {
            for (PatientTreatmentConsumable ptc : updated.getConsumables()) {
                ptc.setPatientTreatment(existing);
                if (ptc.getStockItem() != null && ptc.getStockItem().getId() != null) {
                    StockItem stockItem = stockItemRepository.findById(ptc.getStockItem().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + ptc.getStockItem().getId()));
                    ptc.setStockItem(stockItem);
                }
                existing.getConsumables().add(ptc);
            }
        }

        PatientTreatmentStatus oldStatus = existing.getStatus();
        PatientTreatmentStatus newStatus = updated.getStatus();
        existing.setStatus(newStatus);

        // Status transition logic
        if (newStatus == PatientTreatmentStatus.COMPLETED && oldStatus != PatientTreatmentStatus.COMPLETED) {
            deductStock(existing);
        } else if (newStatus != PatientTreatmentStatus.COMPLETED && oldStatus == PatientTreatmentStatus.COMPLETED) {
            restoreStock(existing);
        }

        return patientTreatmentRepository.save(existing);
    }

    @Transactional
    public void deletePatientTreatment(UUID id) {
        PatientTreatment pt = patientTreatmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient Treatment not found: " + id));

        if (pt.isStockMovementsGenerated()) {
            restoreStock(pt);
        }
        patientTreatmentRepository.deleteById(id);
    }

    private void deductStock(PatientTreatment pt) {
        if (pt.isStockMovementsGenerated()) return;

        // Validation first
        if (pt.getConsumables() != null) {
            for (PatientTreatmentConsumable line : pt.getConsumables()) {
                BigDecimal qty = line.getQuantityUsed();
                if (qty != null && qty.compareTo(BigDecimal.ZERO) > 0) {
                    StockItem stockItem = line.getStockItem();

                    // 1. Decimal Validation
                    if (!stockItem.isDecimalSupported()) {
                        if (qty.stripTrailingZeros().scale() > 0) {
                            throw new IllegalArgumentException("Decimal quantity not supported for item: " + stockItem.getName() +
                                    " (Provided: " + qty + "). Only whole numbers are allowed.");
                        }
                    }

                    // 2. Stock Level Validation
                    BigDecimal currentStock = stockItem.getCurrentStock() != null ? stockItem.getCurrentStock() : BigDecimal.ZERO;
                    if (currentStock.compareTo(qty) < 0) {
                        throw new IllegalArgumentException("Insufficient stock for item: " + stockItem.getName() +
                                " (Available: " + currentStock + ", Required: " + qty + ")");
                    }
                }
            }
        }

        // Deduct
        if (pt.getConsumables() != null) {
            for (PatientTreatmentConsumable line : pt.getConsumables()) {
                BigDecimal qty = line.getQuantityUsed();
                if (qty != null && qty.compareTo(BigDecimal.ZERO) > 0) {
                    stockService.recordMovement(
                            line.getStockItem().getId(),
                            MovementType.OUT,
                            qty,
                            SourceType.PATIENT_TREATMENT,
                            pt.getId(),
                            "PT-" + pt.getId().toString().substring(0, 8).toUpperCase(),
                            "Deducted for Patient Treatment (" + pt.getTreatment().getName() + ")",
                            null
                    );
                }
            }
            pt.setStockMovementsGenerated(true);
        }
    }

    private void restoreStock(PatientTreatment pt) {
        if (!pt.isStockMovementsGenerated()) return;

        if (pt.getConsumables() != null) {
            for (PatientTreatmentConsumable line : pt.getConsumables()) {
                BigDecimal qty = line.getQuantityUsed();
                if (qty != null && qty.compareTo(BigDecimal.ZERO) > 0) {
                    stockService.recordMovement(
                            line.getStockItem().getId(),
                            MovementType.RETURN,
                            qty,
                            SourceType.PATIENT_TREATMENT,
                            pt.getId(),
                            "PT-" + pt.getId().toString().substring(0, 8).toUpperCase(),
                            "Restored due to Patient Treatment status change from COMPLETED",
                            null
                    );
                }
            }
            pt.setStockMovementsGenerated(false);
        }
    }
}
