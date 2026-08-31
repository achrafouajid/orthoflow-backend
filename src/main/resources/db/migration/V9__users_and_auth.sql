CREATE TABLE users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'DOCTOR', 'ASSISTANT')),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users(email);

-- Attributable actors: every mutation from here on records a real user id
-- instead of a random UUID (see billing_audit_log, stock_movements.created_by).
ALTER TABLE billing_audit_log
    ADD CONSTRAINT fk_audit_log_actor FOREIGN KEY (actor_id) REFERENCES users(id);
