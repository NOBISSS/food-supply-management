CREATE TABLE nutrition_requirements (
    id BIGSERIAL PRIMARY KEY,
    beneficiary_id BIGINT NOT NULL REFERENCES beneficiaries(id),
    nutrient VARCHAR(50) NOT NULL,
    recommended_qty NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_nutrition_requirements_beneficiary_id ON nutrition_requirements(beneficiary_id);
