-- AUDIT.md II.7 / P1 item 21 (locking half): no @Version anywhere means two
-- concurrent editors on the same patient/invoice/appointment/etc. silently
-- last-write-wins with no conflict signal. Add a version column to every
-- aggregate root JPA now maps with @Version, defaulting existing rows to 0
-- so the first update after this migration behaves like a fresh entity.
ALTER TABLE patients             ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE invoices             ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE payments             ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE appointments         ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE stock_items          ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE purchase_orders      ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE delivery_notes       ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sales_orders         ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE patient_treatments   ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE treatment_invoices   ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE vendor_invoices      ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE count_sessions       ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE suppliers            ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE treatments           ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE dental_charts        ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE clinical_notes       ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE users                ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
