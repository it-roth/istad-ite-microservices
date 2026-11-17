CREATE TABLE IF NOT EXISTS iam_login_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_uuid UUID,
    principal_name VARCHAR(255) NOT NULL,
    success BOOLEAN NOT NULL,
    method VARCHAR(50) NOT NULL DEFAULT 'PASSWORD',
    user_agent VARCHAR(1000),
    ip_address VARCHAR(64),
    location VARCHAR(255),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_iam_login_history_user_time
    ON iam_login_history(user_uuid, occurred_at DESC);

CREATE TABLE IF NOT EXISTS iam_account_activity (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_uuid UUID,
    event_type VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    actor VARCHAR(255),
    ip_address VARCHAR(64),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_iam_account_activity_user_time
    ON iam_account_activity(user_uuid, occurred_at DESC);

CREATE TABLE IF NOT EXISTS iam_sessions (
    id VARCHAR(128) PRIMARY KEY,
    user_uuid UUID NOT NULL,
    browser VARCHAR(255),
    device VARCHAR(255),
    user_agent VARCHAR(1000),
    ip_address VARCHAR(64),
    location VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_iam_sessions_user ON iam_sessions(user_uuid, revoked_at);

CREATE TABLE IF NOT EXISTS iam_security_configuration (
    id SMALLINT PRIMARY KEY,
    mfa_required BOOLEAN NOT NULL DEFAULT FALSE,
    max_failed_login_attempts INT NOT NULL DEFAULT 5,
    session_timeout_minutes INT NOT NULL DEFAULT 30,
    password_minimum_length INT NOT NULL DEFAULT 8,
    password_require_uppercase BOOLEAN NOT NULL DEFAULT TRUE,
    password_require_number BOOLEAN NOT NULL DEFAULT TRUE,
    password_require_symbol BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO iam_security_configuration(id) VALUES (1)
ON CONFLICT (id) DO NOTHING;
