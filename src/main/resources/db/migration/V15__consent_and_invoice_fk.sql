-- GDPR Art. 6/7 requires a recorded legal basis for processing health
-- data — there was previously no way to capture that a patient (or their
-- guardian, for a minor) consented to their data being processed, nor when
-- (audit V.8). This does not retroactively create consent for existing
-- rows; it gives new registrations somewhere to record it and gives
-- existing patients a null (i.e. "not yet captured") state that the UI can
-- surface and prompt for.
ALTER TABLE patients ADD COLUMN IF NOT EXISTS consent_given_at TIMESTAMPTZ;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS consent_notes TEXT;

-- invoices.patient_id has never had a foreign key (audit II.15/IV.4) —
-- deleting a patient orphaned their invoices. Now that the everyday
-- "delete patient" action is a soft delete (V13), the only path that can
-- still destroy a patients row is the ADMIN-only /erase endpoint — and a
-- hard erase leaving orphaned financial records is exactly the wrong
-- default for data that carries its own accounting-law retention
-- obligation independent of the patient's own GDPR erasure request.
-- RESTRICT (matching payments.invoice_id's existing correct pattern) means
-- an erasure request against a patient with invoices fails loudly instead
-- of silently orphaning them — the operator must first decide how to
-- handle the financial records (anonymize, archive, or confirm they can
-- also be destroyed) rather than have it happen as a side effect.
DO $$
BEGIN
    BEGIN
        ALTER TABLE invoices
            ADD CONSTRAINT fk_invoices_patient
            FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT;
    EXCEPTION WHEN duplicate_object THEN null;
    END;
END
$$;
