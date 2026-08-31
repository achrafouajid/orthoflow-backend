-- Purchase order / delivery note / treatment invoice numbers were generated
-- as `findAll().size() + 1` under a JVM-local `synchronized` — that guards
-- one instance, not the transaction, so two concurrent requests can read the
-- same count and both mint the same number; deleting a row makes the counter
-- go backwards and collide with an existing one. Replaced with atomic
-- Postgres sequences (the pattern billing already used for invoices_seq),
-- backed by a UNIQUE constraint as the real backstop (audit II.4).

CREATE SEQUENCE IF NOT EXISTS po_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS dn_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS treatment_invoice_seq START WITH 1 INCREMENT BY 1;

DO $$
BEGIN
    BEGIN
        ALTER TABLE purchase_orders ADD CONSTRAINT uq_purchase_orders_po_number UNIQUE (po_number);
    EXCEPTION WHEN duplicate_object THEN null;
    END;
END
$$;

DO $$
BEGIN
    BEGIN
        ALTER TABLE delivery_notes ADD CONSTRAINT uq_delivery_notes_dn_number UNIQUE (dn_number);
    EXCEPTION WHEN duplicate_object THEN null;
    END;
END
$$;

DO $$
BEGIN
    BEGIN
        ALTER TABLE treatment_invoices ADD CONSTRAINT uq_treatment_invoices_invoice_number UNIQUE (invoice_number);
    EXCEPTION WHEN duplicate_object THEN null;
    END;
END
$$;

-- Backstop against negative inventory from concurrent deductions racing
-- past the application-level check (audit II.6).
DO $$
BEGIN
    BEGIN
        ALTER TABLE stock_items ADD CONSTRAINT chk_stock_items_current_stock_non_negative CHECK (current_stock >= 0);
    EXCEPTION WHEN duplicate_object THEN null;
    END;
END
$$;
