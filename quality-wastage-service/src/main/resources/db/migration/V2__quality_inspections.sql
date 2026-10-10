CREATE TABLE quality_inspections (
    id BIGSERIAL PRIMARY KEY,
    stock_batch_id BIGINT NOT NULL,
    inspection_date DATE NOT NULL DEFAULT CURRENT_DATE,
    result VARCHAR(20) NOT NULL,
    wastage_quantity NUMERIC(10,2) DEFAULT 0,
    corrective_action VARCHAR(255),
    inspected_by VARCHAR(100)
);
