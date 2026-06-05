-- V7__add_decimal_support_to_stock_items.sql
ALTER TABLE stock_items ADD COLUMN decimal_supported BOOLEAN NOT NULL DEFAULT FALSE;
