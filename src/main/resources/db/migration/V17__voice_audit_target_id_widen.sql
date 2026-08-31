-- `voice_command_audit.target_id` was sized for a single record id, but a
-- dictated finding set is written as one command and audited as one row, with
-- the ids it created joined together. The canonical example from the
-- requirements — "old crown, recurrent caries underneath, crown needs
-- replacement" — is three findings, so three UUIDs plus separators: 110
-- characters against a VARCHAR(96). It would have failed on insert precisely
-- when the multi-finding feature was working as intended.
--
-- TEXT rather than a larger VARCHAR: the number of findings in one utterance
-- has no natural upper bound worth encoding in the schema.
ALTER TABLE voice_command_audit
    ALTER COLUMN target_id TYPE TEXT;
