package com.orthoflow.stock.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "sales_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderLine {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    @JsonIgnore
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem stockItem;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discount;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public BigDecimal calculateLineTotal() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal baseTotal = quantity.multiply(unitPrice);
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discMultiplier = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            return baseTotal.multiply(discMultiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return baseTotal.setScale(2, RoundingMode.HALF_UP);
    }
}
