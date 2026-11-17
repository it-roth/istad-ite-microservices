-- Seed Superadmin User: superadmin / supersecret / lengsaroth9@gmail.com
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
    'a0000000-0000-0000-0000-000000000002',
    'superadmin',
    'lengsaroth9@gmail.com',
    FALSE, -- FALSE = Direct login without 2FA prompt
    '$2a$10$3/3wbarqTBqYPpahjBf5q.Afoi.vJ9FjMh/l0Tjr3VsM8pf2E5nNW', -- BCrypt for 'supersecret'
    'Super',
    'Admin',
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
SET email = EXCLUDED.email,
    password_hash = '$2a$10$3/3wbarqTBqYPpahjBf5q.Afoi.vJ9FjMh/l0Tjr3VsM8pf2E5nNW',
    email_verified = FALSE,
    enabled = TRUE;

-- Assign ADMIN role to superadmin user
INSERT INTO user_roles (user_id, role_id, assigned_at, assigned_by, reason)
SELECT u.id, r.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Super Administrator Role'
FROM users u, roles r
WHERE u.username = 'superadmin' AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id AND ur.revoked_at IS NULL
);
