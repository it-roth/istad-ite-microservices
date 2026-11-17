INSERT INTO permissions (uuid, name, resource, action, description, is_system, is_active, is_deleted,
                         created_at, created_by, updated_at, updated_by, version)
VALUES
 (gen_random_uuid(), 'client:read', 'client', 'read', 'Read registered OAuth clients', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
 (gen_random_uuid(), 'client:create', 'client', 'create', 'Register an OAuth client', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
 (gen_random_uuid(), 'client:delete', 'client', 'delete', 'Delete an OAuth client', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
 (gen_random_uuid(), 'security:read', 'security', 'read', 'Read system security configuration', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
 (gen_random_uuid(), 'security:update', 'security', 'update', 'Update system security configuration', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0)
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id, assigned_at, assigned_by, reason)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Initial system seed for ADMIN'
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN ('client:read', 'client:create', 'client:delete', 'security:read', 'security:update')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.revoked_at IS NULL
  );
