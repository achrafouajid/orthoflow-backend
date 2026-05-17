package com.orthoflow.stock.domain.model;

import com.orthoflow.billing.domain.model.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatment_invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentInvoice {

    @Id
    private UUID id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TreatmentInvoiceStatus status = TreatmentInvoiceStatus.DRAFT;

    @OneToMany(mappedBy = "treatmentInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TreatmentInvoiceConsumable> consumablesUsed = new ArrayList<>();

    @OneToMany(mappedBy = "treatmentInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceDiscount> discounts = new ArrayList<>();

    @Column(name = "treatment_price", nullable = false)
    private BigDecimal treatmentPrice;

    @Column(name = "consumables_cost", nullable = false)
    @Builder.Default
    private BigDecimal consumablesCost = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "stock_movements_generated", nullable = false)
    @Builder.Default
    private boolean stockMovementsGenerated = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "finalized_at")
    private OffsetDateTime finalizedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (sessionDate == null) {
            sessionDate = LocalDate.now();
        }
        calculateTotal();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotal();
    }

    public void calculateTotal() {
        // 1. Calculate base consumables cost (applying item-level discounts to each line if any)
        BigDecimal totalConsumablesCost = BigDecimal.ZERO;
        if (consumablesUsed != null) {
            for (TreatmentInvoiceConsumable line : consumablesUsed) {
                // Compute base line cost
                BigDecimal lineCost = line.getActualQuantity().multiply(line.getPricePerUnit());
                
                // Check for item-level discount
                BigDecimal lineDiscount = BigDecimal.ZERO;
                if (discounts != null) {
                    for (InvoiceDiscount disc : discounts) {
                        if (disc.getTarget() == DiscountTarget.ITEM && line.getStockItem().getId().equals(disc.getTargetId())) {
                            if (disc.getType() == DiscountType.PERCENTAGE) {
                                lineDiscount = lineCost.multiply(disc.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                            } else {
                                lineDiscount = disc.getValue();
                            }
                            break;
                        }
                    }
                }
                
                lineCost = lineCost.subtract(lineDiscount).max(BigDecimal.ZERO);
                line.setTotalCost(lineCost);
                totalConsumablesCost = totalConsumablesCost.add(lineCost);
            }
        }
        this.consumablesCost = totalConsumablesCost;

        // 2. Calculate treatment fee after treatment-level discounts
        BigDecimal actualTreatmentPrice = this.treatmentPrice;
        BigDecimal treatmentDiscount = BigDecimal.ZERO;
        if (discounts != null && actualTreatmentPrice != null) {
            for (InvoiceDiscount disc : discounts) {
                if (disc.getTarget() == DiscountTarget.TREATMENT) {
                    if (disc.getType() == DiscountType.PERCENTAGE) {
                        treatmentDiscount = actualTreatmentPrice.multiply(disc.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    } else {
                        treatmentDiscount = disc.getValue();
                    }
                    break;
                }
            }
        }
        actualTreatmentPrice = actualTreatmentPrice != null ? actualTreatmentPrice.subtract(treatmentDiscount).max(BigDecimal.ZERO) : BigDecimal.ZERO;

        // 3. Compute Subtotal (before invoice-level discount but after item & treatment discounts)
        this.subtotal = actualTreatmentPrice.add(totalConsumablesCost);

        // 4. Apply invoice-level discount
        BigDecimal invoiceDiscount = BigDecimal.ZERO;
        if (discounts != null) {
            for (InvoiceDiscount disc : discounts) {
                if (disc.getTarget() == DiscountTarget.INVOICE) {
                    if (disc.getType() == DiscountType.PERCENTAGE) {
                        invoiceDiscount = this.subtotal.multiply(disc.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    } else {
                        invoiceDiscount = disc.getValue();
                    }
                    break;
                }
            }
        }

        // Final total
        this.total = this.subtotal.subtract(invoiceDiscount).max(BigDecimal.ZERO);
        
        // Sum total discounts for display
        BigDecimal totalItemDiscounts = BigDecimal.ZERO;
        if (consumablesUsed != null) {
            for (TreatmentInvoiceConsumable line : consumablesUsed) {
                BigDecimal baseLineCost = line.getActualQuantity().multiply(line.getPricePerUnit());
                totalItemDiscounts = totalItemDiscounts.add(baseLineCost.subtract(line.getTotalCost()));
            }
        }
        this.discountAmount = totalItemDiscounts.add(treatmentDiscount).add(invoiceDiscount);
    }

    public void addConsumable(TreatmentInvoiceConsumable consumable) {
        consumablesUsed.add(consumable);
        consumable.setTreatmentInvoice(this);
    }

    public void addDiscount(InvoiceDiscount discount) {
        discounts.add(discount);
        discount.setTreatmentInvoice(this);
    }
}
