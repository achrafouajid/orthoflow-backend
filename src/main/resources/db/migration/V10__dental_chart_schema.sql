-- The clinical dental chart was previously localStorage-only: not shared
-- between devices, not backed up, and lost on cache clear. This gives it a
-- durable, auditable home.

CREATE TABLE dental_charts (
    id          UUID PRIMARY KEY,
    patient_id  UUID NOT NULL UNIQUE REFERENCES patients(id) ON DELETE CASCADE,
    chart_type  VARCHAR(10) NOT NULL CHECK (chart_type IN ('adult', 'child')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tooth_states (
    id          UUID PRIMARY KEY,
    chart_id    UUID NOT NULL REFERENCES dental_charts(id) ON DELETE CASCADE,
    fdi         VARCHAR(3) NOT NULL,
    status      VARCHAR(20) NOT NULL,
    notes       TEXT,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (chart_id, fdi)
);

CREATE INDEX idx_tooth_states_chart ON tooth_states(chart_id);

-- Append-only clinical audit trail: who changed which tooth, from what to
-- what, and from where (2D chart / 3D top / 3D frontal / 3D internal / 3D roots).
CREATE TABLE tooth_state_events (
    id               UUID PRIMARY KEY,
    patient_id       UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    fdi              VARCHAR(3) NOT NULL,
    previous_status  VARCHAR(20),
    new_status       VARCHAR(20) NOT NULL,
    notes            TEXT,
    actor_id         UUID NOT NULL REFERENCES users(id),
    source           VARCHAR(20) NOT NULL,
    occurred_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tooth_events_patient ON tooth_state_events(patient_id, occurred_at DESC);
