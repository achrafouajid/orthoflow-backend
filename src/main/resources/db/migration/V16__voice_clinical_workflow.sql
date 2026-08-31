-- Voice-first clinical workflow (audit Part XII phase 4).
--
-- Three groups of tables:
--
--   1. Structured clinical records the voice layer writes into. Until now the
--      only clinical record with a durable home was `tooth_states` — one
--      status and one free-text note per tooth. A dictated examination
--      produces several findings on the same tooth ("old crown, recurrent
--      caries underneath, crown needs replacement"), plus patient-level
--      allergies, medical history and notes that had nowhere to go at all.
--
--   2. `voice_command_audit` — the per-command traceability record required
--      before any speech-driven write touches a clinical record.
--
--   3. `voice_sessions` — a dictated examination, so a whole consultation can
--      be reviewed and confirmed as one unit rather than command by command.
--
-- `tooth_states` is deliberately left in place and still carries the single
-- status the 2D/3D charts colour themselves from; it is now *derived* from
-- the findings below (see DentalChartService#recomputePrimaryStatus) rather
-- than being the whole clinical truth.

-- ── 1. Structured clinical records ──────────────────────────────────────

CREATE TABLE tooth_findings (
    id            UUID PRIMARY KEY,
    chart_id      UUID NOT NULL REFERENCES dental_charts(id) ON DELETE CASCADE,
    fdi           VARCHAR(3) NOT NULL,
    -- Canonical vocabulary shared with the frontend lexicon
    -- (frontend/src/app/core/voice/clinical-lexicon.ts). Kept as a plain
    -- VARCHAR rather than a CHECK constraint so a new finding code can ship
    -- in the lexicon without a migration; unknown codes are rejected at the
    -- application boundary instead.
    finding_code  VARCHAR(48) NOT NULL,
    kind          VARCHAR(24) NOT NULL
                  CHECK (kind IN ('EXISTING', 'CONDITION', 'TREATMENT_REQUIRED', 'OBSERVATION')),
    surface       VARCHAR(24),
    severity      VARCHAR(16) CHECK (severity IS NULL OR severity IN ('MILD', 'MODERATE', 'SEVERE')),
    note          TEXT,
    status        VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
                  CHECK (status IN ('ACTIVE', 'RESOLVED', 'RETRACTED')),
    source        VARCHAR(20) NOT NULL DEFAULT 'manual',
    recorded_by   UUID NOT NULL REFERENCES users(id),
    session_id    UUID,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tooth_findings_chart_fdi ON tooth_findings(chart_id, fdi) WHERE status = 'ACTIVE';
CREATE INDEX idx_tooth_findings_session ON tooth_findings(session_id) WHERE session_id IS NOT NULL;

-- The same finding recorded twice on one tooth is a duplicate, not a second
-- observation — dictation repeats itself ("sixteen caries… sixteen caries").
CREATE UNIQUE INDEX uq_tooth_findings_active
    ON tooth_findings(chart_id, fdi, finding_code)
    WHERE status = 'ACTIVE';

CREATE TABLE clinical_notes (
    id          UUID PRIMARY KEY,
    patient_id  UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    category    VARCHAR(24) NOT NULL
                CHECK (category IN ('GENERAL', 'CHIEF_COMPLAINT', 'OBSERVATION', 'DENTAL_HISTORY',
                                    'MEDICAL_HISTORY', 'DIAGNOSIS', 'FOLLOW_UP', 'TREATMENT_PLAN')),
    content     TEXT NOT NULL,
    -- Set when the doctor attached the note to a specific tooth; NULL means
    -- patient-level. The voice layer must never guess this — it asks.
    fdi         VARCHAR(3),
    author_id   UUID NOT NULL REFERENCES users(id),
    source      VARCHAR(20) NOT NULL DEFAULT 'manual',
    session_id  UUID,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);

CREATE INDEX idx_clinical_notes_patient ON clinical_notes(patient_id, created_at DESC);

CREATE TABLE patient_allergies (
    id           UUID PRIMARY KEY,
    patient_id   UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    substance    VARCHAR(160) NOT NULL,
    reaction     TEXT,
    severity     VARCHAR(16) CHECK (severity IS NULL OR severity IN ('MILD', 'MODERATE', 'SEVERE')),
    source       VARCHAR(20) NOT NULL DEFAULT 'manual',
    recorded_by  UUID NOT NULL REFERENCES users(id),
    session_id   UUID,
    recorded_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at   TIMESTAMPTZ
);

-- Case-insensitive: "Penicillin" and "penicillin" are the same allergy, and a
-- dictated one arrives in whatever case the recogniser chose.
CREATE UNIQUE INDEX uq_patient_allergy_substance
    ON patient_allergies(patient_id, lower(substance))
    WHERE deleted_at IS NULL;

CREATE TABLE patient_medical_history (
    id           UUID PRIMARY KEY,
    patient_id   UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    category     VARCHAR(24) NOT NULL
                 CHECK (category IN ('CONDITION', 'MEDICATION', 'SURGERY', 'DENTAL_HISTORY',
                                     'FAMILY', 'LIFESTYLE', 'OTHER')),
    label        VARCHAR(240) NOT NULL,
    detail       TEXT,
    source       VARCHAR(20) NOT NULL DEFAULT 'manual',
    recorded_by  UUID NOT NULL REFERENCES users(id),
    session_id   UUID,
    recorded_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at   TIMESTAMPTZ
);

CREATE INDEX idx_medical_history_patient ON patient_medical_history(patient_id, category);

-- ── 2. Voice command audit ──────────────────────────────────────────────

-- One row per *interpreted* command, whether or not it executed. Rejected,
-- failed and clarification-needed commands are as important to keep as
-- successful ones: they are the record of what the system heard and chose not
-- to do, which is what a dispute about a clinical record actually turns on.
CREATE TABLE voice_command_audit (
    id                   UUID PRIMARY KEY,
    actor_id             UUID NOT NULL REFERENCES users(id),
    patient_id           UUID REFERENCES patients(id) ON DELETE SET NULL,
    session_id           UUID,
    occurred_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- Retention of the raw utterance is a deployment choice, not a code
    -- choice: audit XII.5 warns against storing transcripts next to patient
    -- identifiers, but a pilot needs them to measure recognition accuracy.
    -- Controlled by orthoflow.voice.audit.store-transcript (default true in
    -- dev, false in prod). NULL here means "deliberately not retained".
    transcript           TEXT,
    locale               VARCHAR(12),
    intent               VARCHAR(64) NOT NULL,
    -- JSON object of resolved entities. TEXT rather than JSONB so the JPA
    -- mapping stays a plain String under ddl-auto:validate.
    entities             TEXT,
    resolver             VARCHAR(16) NOT NULL CHECK (resolver IN ('grammar', 'llm', 'manual')),
    confidence           NUMERIC(4, 3),
    module               VARCHAR(40),
    risk_tier            VARCHAR(16) NOT NULL CHECK (risk_tier IN ('SAFE', 'CONFIRM', 'BLOCKED')),
    confirmation_status  VARCHAR(16) NOT NULL
                         CHECK (confirmation_status IN ('AUTO', 'CONFIRMED', 'REJECTED', 'CANCELLED', 'PENDING')),
    outcome              VARCHAR(16) NOT NULL
                         CHECK (outcome IN ('EXECUTED', 'REJECTED', 'FAILED', 'CLARIFICATION', 'UNDONE')),
    target_type          VARCHAR(48),
    target_id            VARCHAR(96),
    previous_value       TEXT,
    new_value            TEXT,
    error_message        TEXT,
    undone_at            TIMESTAMPTZ
);

CREATE INDEX idx_voice_audit_patient ON voice_command_audit(patient_id, occurred_at DESC);
CREATE INDEX idx_voice_audit_session ON voice_command_audit(session_id) WHERE session_id IS NOT NULL;
CREATE INDEX idx_voice_audit_actor ON voice_command_audit(actor_id, occurred_at DESC);

-- ── 3. Dictated examination sessions ────────────────────────────────────

CREATE TABLE voice_sessions (
    id          UUID PRIMARY KEY,
    patient_id  UUID REFERENCES patients(id) ON DELETE CASCADE,
    actor_id    UUID NOT NULL REFERENCES users(id),
    status      VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE', 'COMPLETED', 'ABANDONED')),
    locale      VARCHAR(12),
    -- JSON snapshot of the consultation summary the doctor confirmed, frozen
    -- at confirmation time so a later edit to the underlying records does not
    -- silently rewrite what was signed off.
    summary     TEXT,
    started_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    ended_at    TIMESTAMPTZ,
    confirmed_at TIMESTAMPTZ
);

CREATE INDEX idx_voice_sessions_patient ON voice_sessions(patient_id, started_at DESC);
