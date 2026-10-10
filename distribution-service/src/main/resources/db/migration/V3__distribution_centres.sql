CREATE TABLE distribution_centres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(150),
    capacity INT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
ALTER TABLE distribution_plans ADD CONSTRAINT fk_distribution_plans_centre
    FOREIGN KEY (distribution_centre_id) REFERENCES distribution_centres(id);
