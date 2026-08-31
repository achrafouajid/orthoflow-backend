package com.orthoflow.treatment.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "invoice_discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDiscount {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_invoice_id", nullable = false)
    @JsonIgnore
    private TreatmentInvoice treatmentInvoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountTarget target;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(nullable = false)
    private BigDecimal value;

    private String reason;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
