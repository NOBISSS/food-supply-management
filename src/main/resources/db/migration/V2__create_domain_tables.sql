CREATE TABLE beneficiaries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    address VARCHAR(255),
    phone VARCHAR(20),
    eligibility_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE food_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    calories_per_unit NUMERIC(10,2),
    protein_per_unit NUMERIC(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expected_delivery DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE distribution_plans (
    id BIGSERIAL PRIMARY KEY,
    beneficiary_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    distribution_centre_id BIGINT NOT NULL,
    planned_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quality_inspections (
    id BIGSERIAL PRIMARY KEY,
    stock_batch_id BIGINT NOT NULL,
    inspection_date DATE NOT NULL DEFAULT CURRENT_DATE,
    result VARCHAR(20) NOT NULL,
    wastage_quantity NUMERIC(10,2) DEFAULT 0,
    corrective_action VARCHAR(255),
    inspected_by VARCHAR(100)
);
