
CREATE TABLE nutrition_requirements (
                                        id                  BIGSERIAL PRIMARY KEY,
                                        beneficiary_id      BIGINT NOT NULL REFERENCES beneficiaries(id),
                                        nutrient            VARCHAR(50) NOT NULL,
                                        recommended_qty     NUMERIC(10,2) NOT NULL,
                                        unit                VARCHAR(20) NOT NULL,
                                        created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_nutrition_requirements_beneficiary_id
    ON nutrition_requirements(beneficiary_id);

CREATE TABLE stock_batches (
                               id                  BIGSERIAL PRIMARY KEY,
                               food_item_id        BIGINT NOT NULL, -- owned by food-item catalog, no cross-domain FK by design
                               batch_no            VARCHAR(50) NOT NULL,
                               quantity            NUMERIC(10,2) NOT NULL,
                               received_date       DATE NOT NULL DEFAULT CURRENT_DATE,
                               expiry_date         DATE,
                               min_stock_level     NUMERIC(10,2) NOT NULL DEFAULT 0,
                               warehouse_location  VARCHAR(100),
                               created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_batches_food_item_id ON stock_batches(food_item_id);
CREATE INDEX idx_stock_batches_expiry_date ON stock_batches(expiry_date);

CREATE TABLE stock_movements (
                                 id              BIGSERIAL PRIMARY KEY,
                                 stock_batch_id  BIGINT NOT NULL REFERENCES stock_batches(id),
                                 movement_type   VARCHAR(10) NOT NULL, -- IN / OUT
                                 quantity        NUMERIC(10,2) NOT NULL,
                                 reason          VARCHAR(100),
                                 moved_at        TIMESTAMP NOT NULL DEFAULT now(),
                                 CONSTRAINT chk_stock_movement_type CHECK (movement_type IN ('IN', 'OUT'))
);

CREATE INDEX idx_stock_movements_stock_batch_id ON stock_movements(stock_batch_id);


CREATE TABLE distribution_centres (
                                      id          BIGSERIAL PRIMARY KEY,
                                      name        VARCHAR(100) NOT NULL,
                                      location    VARCHAR(150),
                                      capacity    INT,
                                      created_at  TIMESTAMP NOT NULL DEFAULT now()
);


ALTER TABLE distribution_plans
    ADD CONSTRAINT fk_distribution_plans_centre
        FOREIGN KEY (distribution_centre_id) REFERENCES distribution_centres(id);

CREATE TABLE purchase_order_items (
                                      id                  BIGSERIAL PRIMARY KEY,
                                      purchase_order_id   BIGINT NOT NULL REFERENCES purchase_orders(id),
                                      food_item_id        BIGINT NOT NULL,
                                      quantity            NUMERIC(10,2) NOT NULL,
                                      unit_price          NUMERIC(10,2),
                                      created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_purchase_order_items_po_id ON purchase_order_items(purchase_order_id);
CREATE INDEX idx_purchase_order_items_food_item_id ON purchase_order_items(food_item_id);

CREATE TABLE nutrition_programmes (
                                      id                      BIGSERIAL PRIMARY KEY,
                                      name                    VARCHAR(150) NOT NULL,
                                      dietary_requirements    VARCHAR(255),
                                      start_date              DATE NOT NULL,
                                      end_date                DATE,
                                      status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                      created_at              TIMESTAMP NOT NULL DEFAULT now(),
                                      CONSTRAINT chk_nutrition_programme_status
                                          CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE programme_participations (
                                          id                      BIGSERIAL PRIMARY KEY,
                                          nutrition_programme_id  BIGINT NOT NULL REFERENCES nutrition_programmes(id),
                                          beneficiary_id          BIGINT NOT NULL REFERENCES beneficiaries(id),
                                          enrolled_date           DATE NOT NULL DEFAULT CURRENT_DATE,
                                          status                  VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
                                          created_at              TIMESTAMP NOT NULL DEFAULT now(),
                                          CONSTRAINT chk_programme_participation_status
                                              CHECK (status IN ('ENROLLED', 'COMPLETED', 'WITHDRAWN')),
                                          CONSTRAINT uq_programme_beneficiary
                                              UNIQUE (nutrition_programme_id, beneficiary_id)
);

CREATE INDEX idx_programme_participations_programme_id
    ON programme_participations(nutrition_programme_id);
CREATE INDEX idx_programme_participations_beneficiary_id
    ON programme_participations(beneficiary_id);

ALTER TABLE quality_inspections
    ADD CONSTRAINT fk_quality_inspections_stock_batch
        FOREIGN KEY (stock_batch_id) REFERENCES stock_batches(id);