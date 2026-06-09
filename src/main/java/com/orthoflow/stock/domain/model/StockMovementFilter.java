package com.orthoflow.stock.domain.model;

/**
 * Immutable value object encapsulating all query parameters for filtering and sorting
 * the stock movement audit ledger. Following SRP, all query concerns are isolated here.
 *
 * @param search       Free-text search matched against stockItem.name, stockItem.sku,
 *                     and sourceReference (case-insensitive).
 * @param movementType MovementType enum name to filter by, or {@code null} / {@code "ALL"} for no filter.
 * @param sortBy       Field name to sort by. Allowed values: createdAt, movementType,
 *                     quantity, quantityBefore, quantityAfter, sourceType, sourceReference.
 * @param sortDir      Sort direction: {@code "ASC"} or {@code "DESC"} (case-insensitive).
 */
public record StockMovementFilter(
        String search,
        String movementType,
        String sortBy,
        String sortDir
) {

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS = java.util.Set.of(
            "createdAt", "movementType", "quantity",
            "quantityBefore", "quantityAfter", "sourceType", "sourceReference"
    );

    private static final String DEFAULT_SORT_BY  = "createdAt";
    private static final String DEFAULT_SORT_DIR = "DESC";

    public StockMovementFilter {
        search       = (search != null && !search.isBlank()) ? search.trim() : null;
        movementType = (movementType != null && !movementType.isBlank()
                        && !"ALL".equalsIgnoreCase(movementType))
                       ? movementType.trim().toUpperCase() : null;
        sortBy       = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy.trim()))
                       ? sortBy.trim() : DEFAULT_SORT_BY;
        sortDir      = ("DESC".equalsIgnoreCase(sortDir != null ? sortDir.trim() : ""))
                       ? "DESC" : DEFAULT_SORT_DIR;
    }

    public boolean isAscending() {
        return "ASC".equals(sortDir);
    }
}
