-- Integrated Food Supply & Nutrition Management
-- V1__initial_schema.sql  (Step 1: Users & Authentication only)
 
CREATE TABLE roles (
    role_id         BIGSERIAL PRIMARY KEY,
    role_name       VARCHAR(50) NOT NULL UNIQUE          -- ADMIN, PROCUREMENT_OFFICER, WAREHOUSE_STAFF, DISTRIBUTION_OFFICER, VIEWER
);
 
CREATE TABLE app_users (
    user_id         BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(150) UNIQUE,
    phone           VARCHAR(20),
    role_id         BIGINT NOT NULL REFERENCES roles(role_id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 
CREATE INDEX idx_app_users_role ON app_users(role_id);
 
-- Seed data so the app has something to log in with / boot against
INSERT INTO roles (role_name) VALUES
    ('ADMIN'), ('PROCUREMENT_OFFICER'), ('WAREHOUSE_STAFF'), ('DISTRIBUTION_OFFICER'), ('VIEWER');
 
INSERT INTO app_users (username, password_hash, full_name, email, role_id)
VALUES ('admin', '$2a$10$replaceWithRealBCryptHash', 'System Admin', 'admin@example.com',
        (SELECT role_id FROM roles WHERE role_name = 'ADMIN'));
 