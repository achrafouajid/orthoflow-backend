CREATE TABLE password_reset_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);

-- The lookup on redemption is by raw token, hashed the same way the write
-- side hashes it before insert, so token_hash is what needs the unique index
-- above, not this one.
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);
