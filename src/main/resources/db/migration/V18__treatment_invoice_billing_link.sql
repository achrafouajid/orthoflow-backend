-- ADR 0005 / AUDIT.md P3 #39: stock.treatment_invoices remains the
-- cost/consumption record (consumables, profitability analytics), but
-- finalizing one now also creates the actual patient-facing financial
-- document in billing.invoices. This column links the two, going forward
-- only — historical rows finalized before this change are left as-is
-- (billing_invoice_id NULL), per the explicit decision not to migrate
-- already-closed sessions.
ALTER TABLE treatment_invoices
    ADD COLUMN billing_invoice_id UUID REFERENCES invoices(id);
