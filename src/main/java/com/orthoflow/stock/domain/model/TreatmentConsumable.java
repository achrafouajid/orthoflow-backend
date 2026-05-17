package com.orthoflow.stock.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "treatment_consumables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentConsumable {

    @EmbeddedId
    @Builder.Default
    private TreatmentConsumableId id = new TreatmentConsumableId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("treatmentId")
    @JoinColumn(name = "treatment_id")
    @JsonIgnore
    private Treatment treatment;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("stockItemId")
    @JoinColumn(name = "stock_item_id")
    private StockItem stockItem;

    @Column(name = "quantity_used", nullable = false)
    private BigDecimal quantityUsed;

    @Column(name = "is_optional", nullable = false)
    @Builder.Default
    private boolean optional = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
        if (treatment != null) {
            this.id.setTreatmentId(treatment.getId());
        }
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
        if (stockItem != null) {
            this.id.setStockItemId(stockItem.getId());
        }
    }
}
