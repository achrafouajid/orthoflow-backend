package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.MovementType;
import com.orthoflow.stock.domain.model.StockMovement;
import com.orthoflow.stock.domain.model.StockMovementFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for building {@link Specification} instances from a {@link StockMovementFilter}.
 * <p>
 * The {@code search} predicate joins to {@code stockItem} to match on its {@code name} and {@code sku},
 * and also matches against {@code sourceReference} on the movement itself.
 */
public final class StockMovementSpecification {

    private StockMovementSpecification() {
        // utility class — not instantiable
    }

    /**
     * Builds a composite {@code Specification<StockMovement>} from all active criteria in the filter.
     */
    public static Specification<StockMovement> from(StockMovementFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── Free-text search across stockItem.name, stockItem.sku, sourceReference
            if (filter.search() != null) {
                String pattern = "%" + filter.search().toLowerCase() + "%";
                Join<Object, Object> itemJoin = root.join("stockItem");
                predicates.add(cb.or(
                        cb.like(cb.lower(itemJoin.get("name")), pattern),
                        cb.like(cb.lower(itemJoin.get("sku")),  pattern),
                        cb.like(cb.lower(root.get("sourceReference")), pattern)
                ));
            }

            // ── MovementType exact match
            if (filter.movementType() != null) {
                try {
                    MovementType type = MovementType.valueOf(filter.movementType());
                    predicates.add(cb.equal(root.get("movementType"), type));
                } catch (IllegalArgumentException ignored) {
                    // unknown type value — skip predicate
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
