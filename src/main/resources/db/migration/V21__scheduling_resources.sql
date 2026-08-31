-- AUDIT.md VIII.6 / P2 item #29 (scheduler rework): appointments had no
-- resource concept and no duration — two overlapping bookings on the same
-- chair could both be saved with no warning. This adds both, enforced with
-- a database-level exclusion constraint rather than an application-layer
-- check-then-act (the same class of race the audit flagged for stock
-- deduction in II.6 — a DB constraint is the only thing that can't be
-- raced by two concurrent requests).
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE chairs (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO chairs (id, name) VALUES
    ('e1000000-0000-0000-0000-000000000001', 'Chair 1'),
    ('e1000000-0000-0000-0000-000000000002', 'Chair 2'),
    ('e1000000-0000-0000-0000-000000000003', 'Chair 3');

ALTER TABLE appointments ADD COLUMN chair_id UUID REFERENCES chairs(id);
ALTER TABLE appointments ADD COLUMN duration_minutes INT NOT NULL DEFAULT 30 CHECK (duration_minutes > 0);

-- timestamptz + interval is STABLE, not IMMUTABLE (its result depends on the
-- session's timezone across DST boundaries), and a GiST exclusion
-- constraint's index expression must be IMMUTABLE. Wrapping the range
-- computation in a SQL function declared IMMUTABLE is the standard fix.
CREATE FUNCTION appointment_slot_range(start_ts TIMESTAMPTZ, minutes INT)
    RETURNS TSTZRANGE
    LANGUAGE sql
    IMMUTABLE
    PARALLEL SAFE
AS $$
    SELECT tstzrange(start_ts, start_ts + (minutes || ' minutes')::interval, '[)');
$$;

-- No two non-cancelled appointments on the same chair may overlap in time.
-- chair_id IS NULL rows are excluded (an unassigned appointment can't
-- conflict with anything on this axis) via the WHERE predicate, since
-- EXCLUDE otherwise treats NULL = NULL as satisfying the equality operator.
ALTER TABLE appointments ADD CONSTRAINT appointments_no_chair_overlap
    EXCLUDE USING gist (
        chair_id WITH =,
        appointment_slot_range(date_time, duration_minutes) WITH &&
    ) WHERE (chair_id IS NOT NULL AND status NOT IN ('CANCELLED', 'NO_SHOW'));
