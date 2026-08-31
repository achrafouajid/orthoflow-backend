-- AUDIT.md VIII.6 / P2 item #29: the schedule's working hours (8 AM-7 PM)
-- were hardcoded in the frontend with no way for a clinic to configure its
-- own hours. Single-tenant-per-deployment (ADR 0002), so this is a
-- singleton row rather than one row per practice.
CREATE TABLE practice_settings (
    id UUID PRIMARY KEY,
    working_hours_start SMALLINT NOT NULL DEFAULT 8 CHECK (working_hours_start >= 0 AND working_hours_start <= 23),
    working_hours_end SMALLINT NOT NULL DEFAULT 19 CHECK (working_hours_end >= 1 AND working_hours_end <= 24),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_practice_settings_hours_order CHECK (working_hours_start < working_hours_end)
);

INSERT INTO practice_settings (id, working_hours_start, working_hours_end)
VALUES ('00000000-0000-0000-0000-000000000001', 8, 19);
