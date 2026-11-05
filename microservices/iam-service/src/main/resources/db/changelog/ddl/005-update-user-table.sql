-- liquibase formatted sql

-- changeset mac:1793783334187-16
ALTER TABLE roles_permissions DROP CONSTRAINT fk_rolper_on_permission;

-- changeset mac:1793783334187-17
ALTER TABLE roles_permissions DROP CONSTRAINT fk_rolper_on_role;

-- changeset mac:1793783334187-18
ALTER TABLE users_permissions DROP CONSTRAINT fk_useper_on_permission;

-- changeset mac:1793783334187-19
ALTER TABLE users_permissions DROP CONSTRAINT fk_useper_on_user;

-- changeset mac:1793783334187-5
CREATE TABLE users_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);

-- changeset mac:1793783334187-6
ALTER TABLE roles_permissions
    ADD permission_id BIGINT;
ALTER TABLE roles_permissions
    ADD role_id BIGINT;

-- changeset mac:1793783334187-7
ALTER TABLE users_permissions
    ADD permission_id BIGINT;
ALTER TABLE users_permissions
    ADD user_id BIGINT;

-- changeset mac:1793783334187-10
ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

-- changeset mac:1793783334187-11
ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

-- changeset mac:1793783334187-12
ALTER TABLE users_permissions
    ADD CONSTRAINT fk_useper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

-- changeset mac:1793783334187-13
ALTER TABLE users_permissions
    ADD CONSTRAINT fk_useper_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset mac:1793783334187-14
ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

-- changeset mac:1793783334187-15
ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset mac:1793783334187-23
ALTER TABLE roles_permissions DROP COLUMN permissions_id;
ALTER TABLE roles_permissions DROP COLUMN users_id;

-- changeset mac:1793783334187-24
ALTER TABLE users_permissions DROP COLUMN permissions_id;
ALTER TABLE users_permissions DROP COLUMN users_id;

-- changeset mac:1793783334187-2
ALTER TABLE roles_permissions
    ADD CONSTRAINT pk_roles_permissions PRIMARY KEY (permission_id, role_id);

-- changeset mac:1793783334187-4
ALTER TABLE users_permissions
    ADD CONSTRAINT pk_users_permissions PRIMARY KEY (permission_id, user_id);

