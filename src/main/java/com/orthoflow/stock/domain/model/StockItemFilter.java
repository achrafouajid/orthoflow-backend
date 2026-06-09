package com.orthoflow.stock.domain.model;

/**
 * Immutable value object encapsulating all query parameters for filtering and sorting
 * the stock item catalog. Following SRP, all query concerns are isolated here.
 *
 * @param search   Free-text search matched against item name and SKU (case-insensitive).
 * @param category StockCategory enum name to filter by, or {@code null} / {@code "ALL"} for no filter.
 * @param sortBy   Field name to sort by. Allowed values: name, sku, category,
 *                 currentStock, minimumStock, purchasePrice, pricePerUse, createdAt.
 * @param sortDir  Sort direction: {@code "ASC"} or {@code "DESC"} (case-insensitive).
 */
public record StockItemFilter(
        String search,
        String category,
        String sortBy,
        String sortDir
) {

    /** Whitelisted sortable fields to prevent SQL-injection via column name. */
    private static final java.util.Set<String> ALLOWED_SORT_FIELDS = java.util.Set.of(
            "name", "sku", "category", "currentStock", "minimumStock",
            "purchasePrice", "pricePerUse", "createdAt", "updatedAt"
    );

    private static final String DEFAULT_SORT_BY  = "name";
    private static final String DEFAULT_SORT_DIR = "ASC";

    /**
     * Compact canonical constructor — normalises and validates inputs.
     */
    public StockItemFilter {
        search   = (search  != null && !search.isBlank())  ? search.trim()  : null;
        category = (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category))
                   ? category.trim().toUpperCase() : null;
        sortBy   = (sortBy  != null && ALLOWED_SORT_FIELDS.contains(sortBy.trim()))
                   ? sortBy.trim() : DEFAULT_SORT_BY;
        sortDir  = ("DESC".equalsIgnoreCase(sortDir != null ? sortDir.trim() : ""))
                   ? "DESC" : DEFAULT_SORT_DIR;
    }

    public boolean isAscending() {
        return "ASC".equals(sortDir);
    }
}
