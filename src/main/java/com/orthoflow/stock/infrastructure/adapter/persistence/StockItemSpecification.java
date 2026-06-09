package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockCategory;
import com.orthoflow.stock.domain.model.StockItem;
import com.orthoflow.stock.domain.model.StockItemFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for building {@link Specification} instances from a {@link StockItemFilter}.
 * <p>
 * Each method follows SRP — one method, one concern.
 * Composed via the Specification combinator pattern (ISP: each spec is minimal).
 */
public final class StockItemSpecification {

    private StockItemSpecification() {
        // utility class — not instantiable
    }

    /**
     * Builds a composite {@code Specification<StockItem>} from all active criteria in the filter.
     */
    public static Specification<StockItem> from(StockItemFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── Free-text search: name ILIKE %q% OR sku ILIKE %q%
            if (filter.search() != null) {
                String pattern = "%" + filter.search().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("sku")),  pattern)
                ));
            }

            // ── Category exact match
            if (filter.category() != null) {
                try {
                    StockCategory cat = StockCategory.valueOf(filter.category());
                    predicates.add(cb.equal(root.get("category"), cat));
                } catch (IllegalArgumentException ignored) {
                    // unknown category value — skip the predicate, return all
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
