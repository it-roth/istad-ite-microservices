-- Remove all school management & non-ecommerce permissions and their role assignments
DELETE FROM role_permissions WHERE permission_id IN (
    SELECT id FROM permissions WHERE resource IN (
        'document', 'document:template', 'high-degree', 'high-degree:calendar',
        'high-degree:class', 'high-degree:enrollment', 'high-degree:program',
        'high-degree:result', 'academic:course', 'academic:curriculum',
        'academic:department', 'academic:faculty', 'academic:major',
        'academic:semester', 'academic:subject', 'admission:applicant',
        'admission:application', 'attendance:record', 'attendance:session',
        'finance:fee', 'finance:invoice', 'finance:payment'
    )
);

DELETE FROM permissions WHERE resource IN (
    'document', 'document:template', 'high-degree', 'high-degree:calendar',
    'high-degree:class', 'high-degree:enrollment', 'high-degree:program',
    'high-degree:result', 'academic:course', 'academic:curriculum',
    'academic:department', 'academic:faculty', 'academic:major',
    'academic:semester', 'academic:subject', 'admission:applicant',
    'admission:application', 'attendance:record', 'attendance:session',
    'finance:fee', 'finance:invoice', 'finance:payment'
);

-- Seed fresh E-Commerce microservices permissions for the platform services:
-- 1. IAM, 2. CUSTOMER, 3. BUSINESS, 4. ORDER, 5. PAYMENT, 6. MEDIA
INSERT INTO permissions (uuid, name, resource, action, description, is_system, is_active, is_deleted, created_at, created_by, updated_at, updated_by, version)
VALUES
    -- 1. IAM SERVICE (iam-service / db_oauth2)
    (gen_random_uuid(), 'iam:user:read', 'iam:user', 'read', 'Read user profiles and authorities', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:user:create', 'iam:user', 'create', 'Create new user accounts', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:user:update', 'iam:user', 'update', 'Update user profiles, status, or credentials', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:user:delete', 'iam:user', 'delete', 'Delete or disable user accounts', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:user:assign', 'iam:user', 'assign', 'Assign roles and direct permissions to users', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:role:read', 'iam:role', 'read', 'Read roles and assigned authorities', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:role:create', 'iam:role', 'create', 'Create new RBAC roles', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:role:update', 'iam:role', 'update', 'Update role status and metadata', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:role:delete', 'iam:role', 'delete', 'Delete custom roles', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:role:assign', 'iam:role', 'assign', 'Assign permissions to roles', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:permission:read', 'iam:permission', 'read', 'Read system permissions catalog', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:permission:create', 'iam:permission', 'create', 'Define new permissions', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:permission:update', 'iam:permission', 'update', 'Update permission metadata', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:permission:delete', 'iam:permission', 'delete', 'Delete permissions from catalog', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:client:read', 'iam:client', 'read', 'Read OAuth2/OIDC clients', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:client:create', 'iam:client', 'create', 'Register new OAuth2 applications', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:client:update', 'iam:client', 'update', 'Update client secret and redirect URIs', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:client:delete', 'iam:client', 'delete', 'Deregister OAuth2 applications', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:security:read', 'iam:security', 'read', 'Read security and MFA policy configurations', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:security:update', 'iam:security', 'update', 'Update token lifetimes and MFA rules', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:session:read', 'iam:session', 'read', 'List active user login sessions', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:session:delete', 'iam:session', 'delete', 'Terminate user sessions remotely', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:audit:read', 'iam:audit', 'read', 'Inspect security and admin audit logs', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'iam:invitation:create', 'iam:invitation', 'create', 'Invite staff and administrators to platform', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    -- 2. CUSTOMER SERVICE (customer-service / db_customer)
    (gen_random_uuid(), 'customer:customer:read', 'customer:customer', 'read', 'Read customer profiles and shopping history', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:customer:create', 'customer:customer', 'create', 'Register new customers', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:customer:update', 'customer:customer', 'update', 'Update customer preferences and profile', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:customer:delete', 'customer:customer', 'delete', 'Deactivate or delete customer accounts', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:kyc:read', 'customer:kyc', 'read', 'View customer KYC identity verification records', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:kyc:verify', 'customer:kyc', 'verify', 'Approve or reject customer KYC verification', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:address:read', 'customer:address', 'read', 'Read customer delivery addresses', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'customer:address:manage', 'customer:address', 'manage', 'Manage and update delivery addresses', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    -- 3. BUSINESS SERVICE (business-service / db_business - Product & Catalog)
    (gen_random_uuid(), 'business:product:read', 'business:product', 'read', 'Read store product details and pricing', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:product:create', 'business:product', 'create', 'Add new products to catalog', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:product:update', 'business:product', 'update', 'Update product information, SKU, and price', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:product:delete', 'business:product', 'delete', 'Remove products from catalog', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:category:read', 'business:category', 'read', 'Read product categories hierarchy', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:category:create', 'business:category', 'create', 'Create new product categories', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:category:update', 'business:category', 'update', 'Update product categories', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:category:delete', 'business:category', 'delete', 'Delete product categories', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:inventory:read', 'business:inventory', 'read', 'Check stock levels and warehouse inventory', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:inventory:manage', 'business:inventory', 'manage', 'Adjust stock levels and restock items', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:inventory:update', 'business:inventory', 'update', 'Update warehouse inventory records', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:brand:read', 'business:brand', 'read', 'Read product brands list', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:brand:create', 'business:brand', 'create', 'Add new manufacturer brands', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'business:brand:update', 'business:brand', 'update', 'Update manufacturer brand details', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    -- 4. ORDER SERVICE (order-service / db_order)
    (gen_random_uuid(), 'order:order:read', 'order:order', 'read', 'View customer orders and order history', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:order:create', 'order:order', 'create', 'Place and initiate new customer orders', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:order:update', 'order:order', 'update', 'Update order progress and shipping status', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:order:cancel', 'order:order', 'cancel', 'Cancel pending or unfulfilled orders', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:order:manage', 'order:order', 'manage', 'Administer and override order workflows', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:cart:read', 'order:cart', 'read', 'View items in customer shopping cart', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:cart:update', 'order:cart', 'update', 'Add or update items in shopping cart', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:cart:clear', 'order:cart', 'clear', 'Clear shopping cart contents', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:checkout:process', 'order:checkout', 'process', 'Process cart checkout into order', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'order:tracking:read', 'order:tracking', 'read', 'Track delivery package coordinates and status', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    -- 5. PAYMENT SERVICE (payment-service / db_payment)
    (gen_random_uuid(), 'payment:payment:read', 'payment:payment', 'read', 'Read payment status and receipts', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:payment:process', 'payment:payment', 'process', 'Charge customer cards or QR payments', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:payment:refund', 'payment:payment', 'refund', 'Issue customer payment refunds', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:transaction:read', 'payment:transaction', 'read', 'Inspect payment transaction ledger', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:transaction:manage', 'payment:transaction', 'manage', 'Reconcile and manage payment transactions', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:method:read', 'payment:method', 'read', 'Read saved customer payment methods', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:method:manage', 'payment:method', 'manage', 'Manage accepted payment gateways and cards', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'payment:invoice:read', 'payment:invoice', 'read', 'Generate and view tax invoices', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),

    -- 6. MEDIA SERVICE (media-service / db_media)
    (gen_random_uuid(), 'media:file:upload', 'media:file', 'upload', 'Upload product images and documents to storage', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'media:file:read', 'media:file', 'read', 'Access and download media assets', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'media:file:delete', 'media:file', 'delete', 'Delete unused media assets', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0),
    (gen_random_uuid(), 'media:image:optimize', 'media:image', 'optimize', 'Generate responsive thumbnail images', TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0)
ON CONFLICT (name) DO UPDATE SET
    resource = EXCLUDED.resource,
    action = EXCLUDED.action,
    description = EXCLUDED.description,
    is_system = EXCLUDED.is_system,
    is_active = EXCLUDED.is_active,
    is_deleted = EXCLUDED.is_deleted;

-- Revoke any non-ecommerce permissions from ADMIN
DELETE FROM role_permissions
WHERE role_id = (SELECT id FROM roles WHERE name = 'ADMIN')
  AND permission_id IN (
    SELECT id FROM permissions WHERE name NOT LIKE 'iam:%'
      AND name NOT LIKE 'customer:%'
      AND name NOT LIKE 'business:%'
      AND name NOT LIKE 'order:%'
      AND name NOT LIKE 'payment:%'
      AND name NOT LIKE 'media:%'
  );

-- Assign all 68 E-Commerce permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id, assigned_at, assigned_by, reason)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM', 'E-Commerce Platform seed for ADMIN'
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND (
    p.name LIKE 'iam:%'
    OR p.name LIKE 'customer:%'
    OR p.name LIKE 'business:%'
    OR p.name LIKE 'order:%'
    OR p.name LIKE 'payment:%'
    OR p.name LIKE 'media:%'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.revoked_at IS NULL
  );

-- Also assign standard shopping permissions to USER role
INSERT INTO role_permissions (role_id, permission_id, assigned_at, assigned_by, reason)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM', 'Standard customer shopping permissions'
FROM roles r, permissions p
WHERE r.name = 'USER'
  AND p.name IN (
    'iam:user:read',
    'customer:customer:read',
    'customer:customer:update',
    'customer:address:read',
    'customer:address:manage',
    'business:product:read',
    'business:category:read',
    'business:brand:read',
    'order:cart:read',
    'order:cart:update',
    'order:cart:clear',
    'order:checkout:process',
    'order:order:create',
    'order:order:read',
    'order:tracking:read',
    'payment:payment:read',
    'payment:method:read',
    'media:file:read'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.revoked_at IS NULL
  );
