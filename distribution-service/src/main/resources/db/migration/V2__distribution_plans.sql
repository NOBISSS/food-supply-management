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
