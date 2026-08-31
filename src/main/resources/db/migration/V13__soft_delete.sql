-- Hard deletes on clinical and financial records violate statutory
-- retention obligations, and deleting a patient cascaded to their
-- appointment and treatment history (audit II.15). Deletion through the
-- normal product UI is now an archive (deleted_at set); a genuinely
-- separate, audited hard-delete path is required for GDPR/Law 09-08
-- erasure requests.

-- Invoices have no delete endpoint at all today, so there is no hard-delete
-- behaviour to fix yet; add deleted_at/deleted_by here too once one exists.

ALTER TABLE patients ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE patients ADD COLUMN deleted_by UUID REFERENCES users(id);

ALTER TABLE patient_treatments ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE patient_treatments ADD COLUMN deleted_by UUID REFERENCES users(id);

-- appointments.patient_id and patient_treatments.patient_id still cascade on
-- a *hard* delete of a patient (the dedicated erasure path); a normal
-- archive no longer deletes the row at all, so the cascade never fires.
