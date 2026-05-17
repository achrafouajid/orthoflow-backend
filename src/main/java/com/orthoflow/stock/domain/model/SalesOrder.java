package com.orthoflow.stock.domain.model;

import com.orthoflow.billing.domain.model.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {

    @Id
    private UUID id;

    @Column(name = "so_number", unique = true, nullable = false)
    private String soNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SOStatus status = SOStatus.DRAFT;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderLine> lines = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        calculateTotal();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotal();
    }

    public void calculateTotal() {
        if (lines != null) {
            this.totalAmount = lines.stream()
                    .map(SalesOrderLine::calculateLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void addLine(SalesOrderLine line) {
        lines.add(line);
        line.setSalesOrder(this);
    }
}
