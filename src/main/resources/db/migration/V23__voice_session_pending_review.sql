-- A dictated examination that has ended but has not yet been written to the
-- clinical record.
--
-- The voice workflow used to write each finding as it was dictated, so a
-- session was either running or finished. It now buffers: commands are
-- audited immediately (so nothing said is ever lost, even to a browser
-- crash) but the clinical tables are written only once the dentist has read
-- the generated summary, corrected what was misheard and removed what does
-- not belong. That review sits between ACTIVE and COMPLETED and had no state
-- of its own, which left a consultation that was dictated but never saved
-- looking exactly like one that was saved.
--
-- No backfill: every existing row is ACTIVE, COMPLETED or ABANDONED and all
-- three keep their meaning. Sessions from before this migration were written
-- as they were dictated, so none of them are awaiting anything.

ALTER TABLE voice_sessions DROP CONSTRAINT voice_sessions_status_check;

ALTER TABLE voice_sessions ADD CONSTRAINT voice_sessions_status_check
    CHECK (status IN ('ACTIVE', 'PENDING_REVIEW', 'COMPLETED', 'ABANDONED'));

-- The review queue: which examinations still need a dentist to sign them off.
CREATE INDEX idx_voice_sessions_pending_review
    ON voice_sessions(actor_id, ended_at DESC)
    WHERE status = 'PENDING_REVIEW';
