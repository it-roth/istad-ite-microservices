-- Seed Default Admin User: admin / admin123
INSERT INTO users (
    uuid,
    username,
    email,
    email_verified,
    password_hash,
    given_name,
    family_name,
    locale,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    is_deleted,
    created_at,
    created_by,
    updated_at,
    updated_by,
    version
)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'admin',
    'admin@istad.edu.kh',
    FALSE, -- Set to FALSE so you can log in directly without 2FA prompt
    '$2a$10$b5V1.KfXJkVAGfp2kQmitOjs3TqpKO0o3ME1C5PjvHBqZIjESbXz6', -- BCrypt for 'admin123'
    'System',
    'Administrator',
    'en',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    'SYSTEM',
    CURRENT_TIMESTAMP,
    'SYSTEM',
    0
)
ON CONFLICT (username) DO UPDATE
SET password_hash = '$2a$10$b5V1.KfXJkVAGfp2kQmitOjs3TqpKO0o3ME1C5PjvHBqZIjESbXz6',
    enabled = TRUE;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id, assigned_at, assigned_by, reason)
SELECT u.id, r.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Default Administrator Role'
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id AND ur.revoked_at IS NULL
);
