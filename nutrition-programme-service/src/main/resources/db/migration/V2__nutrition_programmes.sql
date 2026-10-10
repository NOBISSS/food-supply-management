CREATE TABLE nutrition_programmes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    dietary_requirements VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE programme_participations (
    id BIGSERIAL PRIMARY KEY,
    nutrition_programme_id BIGINT NOT NULL REFERENCES nutrition_programmes(id),
    beneficiary_id BIGINT NOT NULL,
    enrolled_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED' CHECK (status IN ('ENROLLED', 'COMPLETED', 'WITHDRAWN')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_programme_beneficiary UNIQUE (nutrition_programme_id, beneficiary_id)
);
CREATE INDEX idx_programme_participations_programme_id ON programme_participations(nutrition_programme_id);
CREATE INDEX idx_programme_participations_beneficiary_id ON programme_participations(beneficiary_id);
