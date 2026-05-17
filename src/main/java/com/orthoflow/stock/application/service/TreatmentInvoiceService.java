package com.orthoflow.stock.application.service;

import com.orthoflow.billing.domain.model.Patient;
import com.orthoflow.billing.domain.repository.PatientRepository;
import com.orthoflow.stock.domain.model.*;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import com.orthoflow.stock.domain.repository.TreatmentInvoiceRepository;
import com.orthoflow.stock.domain.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreatmentInvoiceService {

    private final TreatmentInvoiceRepository treatmentInvoiceRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final StockItemRepository stockItemRepository;
    private final StockService stockService;

    public List<TreatmentInvoice> getAllInvoices() {
        return treatmentInvoiceRepository.findAll();
    }

    public Optional<TreatmentInvoice> getInvoiceById(UUID id) {
        return treatmentInvoiceRepository.findById(id);
    }

    public Optional<TreatmentInvoice> getInvoiceByNumber(String invoiceNumber) {
        return treatmentInvoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    public List<TreatmentInvoice> getInvoicesByPatient(UUID patientId) {
        return treatmentInvoiceRepository.findByPatientId(patientId);
    }

    @Transactional
    public TreatmentInvoice createDraftFromTreatment(UUID patientId, UUID treatmentId, UUID createdBy) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Treatment not found: " + treatmentId));

        TreatmentInvoice invoice = TreatmentInvoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .patient(patient)
                .treatment(treatment)
                .sessionDate(LocalDate.now())
                .status(TreatmentInvoiceStatus.DRAFT)
                .treatmentPrice(treatment.getBasePrice())
                .createdBy(createdBy != null ? createdBy : UUID.randomUUID())
                .build();

        // Add default consumables configured for the treatment
        if (treatment.getConsumables() != null) {
            for (TreatmentConsumable defaultConsumable : treatment.getConsumables()) {
                TreatmentInvoiceConsumable line = TreatmentInvoiceConsumable.builder()
                        .treatmentInvoice(invoice)
                        .stockItem(defaultConsumable.getStockItem())
                        .defaultQuantity(defaultConsumable.getQuantityUsed())
                        .actualQuantity(defaultConsumable.getQuantityUsed())
                        .pricePerUnit(defaultConsumable.getStockItem().getPricePerUse())
                        .modified(false)
                        .build();
                line.calculateTotalCost();
                invoice.addConsumable(line);
            }
        }

        invoice.calculateTotal();
        return treatmentInvoiceRepository.save(invoice);
    }

    @Transactional
    public TreatmentInvoice saveInvoice(TreatmentInvoice invoice) {
        if (invoice.getId() == null) {
            invoice.setId(UUID.randomUUID());
        }

        // Re-attach objects
        if (invoice.getPatient() != null && invoice.getPatient().getId() != null) {
            Patient patient = patientRepository.findById(invoice.getPatient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + invoice.getPatient().getId()));
            invoice.setPatient(patient);
        }
        if (invoice.getTreatment() != null && invoice.getTreatment().getId() != null) {
            Treatment treatment = treatmentRepository.findById(invoice.getTreatment().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Treatment not found: " + invoice.getTreatment().getId()));
            invoice.setTreatment(treatment);
        }

        if (invoice.getConsumablesUsed() != null) {
            for (TreatmentInvoiceConsumable line : invoice.getConsumablesUsed()) {
                line.setTreatmentInvoice(invoice);
                if (line.getStockItem() != null && line.getStockItem().getId() != null) {
                    StockItem stockItem = stockItemRepository.findById(line.getStockItem().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Stock Item not found: " + line.getStockItem().getId()));
                    line.setStockItem(stockItem);
                }
            }
        }

        if (invoice.getDiscounts() != null) {
            for (InvoiceDiscount disc : invoice.getDiscounts()) {
                disc.setTreatmentInvoice(invoice);
            }
        }

        invoice.calculateTotal();
        return treatmentInvoiceRepository.save(invoice);
    }

    @Transactional
    public TreatmentInvoice finalizeInvoice(UUID id, UUID finalizedBy) {
        TreatmentInvoice invoice = treatmentInvoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));

        if (invoice.getStatus() != TreatmentInvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be finalized.");
        }

        invoice.setStatus(TreatmentInvoiceStatus.FINALIZED);
        invoice.setFinalizedAt(OffsetDateTime.now());

        // Deduct consumables stock and record movements
        if (!invoice.isStockMovementsGenerated() && invoice.getConsumablesUsed() != null) {
            for (TreatmentInvoiceConsumable line : invoice.getConsumablesUsed()) {
                BigDecimal qtyUsed = line.getActualQuantity();
                if (qtyUsed != null && qtyUsed.compareTo(BigDecimal.ZERO) > 0) {
                    stockService.recordMovement(
                            line.getStockItem().getId(),
                            MovementType.OUT,
                            qtyUsed,
                            SourceType.TREATMENT_INVOICE,
                            invoice.getId(),
                            invoice.getInvoiceNumber(),
                            "Deducted for treatment session (" + invoice.getTreatment().getName() + ")",
                            finalizedBy
                    );
                }
            }
            invoice.setStockMovementsGenerated(true);
        }

        return treatmentInvoiceRepository.save(invoice);
    }

    @Transactional
    public TreatmentInvoice cancelInvoice(UUID id, UUID cancelledBy) {
        TreatmentInvoice invoice = treatmentInvoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));

        if (invoice.getStatus() == TreatmentInvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Invoice is already cancelled.");
        }

        // Restore stock if movements were previously generated
        if (invoice.isStockMovementsGenerated() && invoice.getConsumablesUsed() != null) {
            for (TreatmentInvoiceConsumable line : invoice.getConsumablesUsed()) {
                BigDecimal qtyUsed = line.getActualQuantity();
                if (qtyUsed != null && qtyUsed.compareTo(BigDecimal.ZERO) > 0) {
                    stockService.recordMovement(
                            line.getStockItem().getId(),
                            MovementType.RETURN,
                            qtyUsed,
                            SourceType.TREATMENT_INVOICE,
                            invoice.getId(),
                            invoice.getInvoiceNumber(),
                            "Stock restored due to Treatment Invoice cancellation: " + invoice.getInvoiceNumber(),
                            cancelledBy
                    );
                }
            }
            invoice.setStockMovementsGenerated(false);
        }

        invoice.setStatus(TreatmentInvoiceStatus.CANCELLED);
        return treatmentInvoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(UUID id) {
        TreatmentInvoice invoice = treatmentInvoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
        if (invoice.getStatus() == TreatmentInvoiceStatus.FINALIZED) {
            throw new IllegalStateException("Finalized invoices cannot be deleted.");
        }
        treatmentInvoiceRepository.deleteById(id);
    }

    private synchronized String generateInvoiceNumber() {
        int nextVal = treatmentInvoiceRepository.findAll().size() + 1;
        return "TINV-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextVal);
    }
}
