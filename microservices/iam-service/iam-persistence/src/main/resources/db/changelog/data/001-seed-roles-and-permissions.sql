-- Seed Roles: ADMIN and USER
INSERT INTO roles (uuid, name, description, is_system, is_active, is_deleted, created_at, created_by, updated_at, updated_by, version)
VALUES 
    (gen_random_uuid(), 'ADMIN', 'System Administrator with full privileges', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'USER', 'Default standard user', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0)
ON CONFLICT (name) DO NOTHING;

-- Seed Permissions for user, role, permission
INSERT INTO permissions (uuid, name, resource, action, description, is_system, is_active, is_deleted, created_at, created_by, updated_at, updated_by, version)
VALUES
    (gen_random_uuid(), 'user:create', 'user', 'create', 'Create a new user', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'user:read', 'user', 'read', 'Read user profiles and authorities', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'user:update', 'user', 'update', 'Update user profiles, status, or credentials', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'user:delete', 'user', 'delete', 'Delete or soft-delete a user', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'user:assign', 'user', 'assign', 'Assign or revoke roles and direct permissions for a user', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    (gen_random_uuid(), 'role:create', 'role', 'create', 'Create a new role', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'role:read', 'role', 'read', 'Read role details and listings', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'role:update', 'role', 'update', 'Update role details and active status', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'role:delete', 'role', 'delete', 'Delete a role', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'role:assign', 'role', 'assign', 'Assign or revoke permissions for a role', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    (gen_random_uuid(), 'permission:create', 'permission', 'create', 'Create a new permission', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'permission:read', 'permission', 'read', 'Read permission details and listings', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'permission:update', 'permission', 'update', 'Update permission details and active status', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'permission:delete', 'permission', 'delete', 'Delete a permission', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0)
ON CONFLICT (name) DO NOTHING;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id, assigned_at, assigned_by, reason)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Initial system seed for ADMIN'
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.revoked_at IS NULL
);

-- Assign user:read to USER role
INSERT INTO role_permissions (role_id, permission_id, assigned_at, assigned_by, reason)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Initial system seed for USER'
FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name = 'user:read'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.revoked_at IS NULL
);

