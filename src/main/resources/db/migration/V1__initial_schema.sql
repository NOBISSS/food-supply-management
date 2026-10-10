-- V2__food_supply_schema.sql
-- Integrated Food Supply & Nutrition Management
-- Step 2: Beneficiary, Catalog, Procurement, Inventory, Distribution, Nutrition Programme, Quality & Wastage

-- =========================================================
-- BENEFICIARY MANAGEMENT
-- =========================================================

CREATE TABLE beneficiary_profiles (
                                      beneficiary_id      BIGSERIAL PRIMARY KEY,
                                      full_name            VARCHAR(150) NOT NULL,
                                      gender                VARCHAR(10),
                                      date_of_birth         DATE,
                                      phone                 VARCHAR(20),
                                      address               VARCHAR(255),
                                      household_id          VARCHAR(50),
                                      registered_date       DATE NOT NULL DEFAULT CURRENT_DATE,
                                      status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'   -- ACTIVE, INACTIVE
);

CREATE TABLE beneficiary_nutrition_requirements (
                                                    requirement_id       BIGSERIAL PRIMARY KEY,
                                                    beneficiary_id        BIGINT NOT NULL REFERENCES beneficiary_profiles(beneficiary_id),
                                                    nutrient_type          VARCHAR(50) NOT NULL,                  -- calories, protein, etc.
                                                    required_quantity      DECIMAL(10,2) NOT NULL,
                                                    unit                   VARCHAR(20) NOT NULL
);

CREATE TABLE beneficiary_eligibility (
                                         eligibility_id        BIGSERIAL PRIMARY KEY,
                                         beneficiary_id          BIGINT NOT NULL REFERENCES beneficiary_profiles(beneficiary_id),
                                         programme_type           VARCHAR(100),
                                         eligibility_criteria     VARCHAR(255),
                                         verified_status           VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
                                         verified_date              DATE
);

CREATE INDEX idx_benef_nutrition_beneficiary ON beneficiary_nutrition_requirements(beneficiary_id);
CREATE INDEX idx_benef_eligibility_beneficiary ON beneficiary_eligibility(beneficiary_id);


-- =========================================================
-- FOOD ITEM & NUTRITION CATALOG
-- =========================================================

CREATE TABLE catalog_food_categories (
                                         category_id     BIGSERIAL PRIMARY KEY,
                                         category_name    VARCHAR(100) NOT NULL UNIQUE,
                                         description       VARCHAR(255)
);

CREATE TABLE catalog_food_items (
                                    food_item_id         BIGSERIAL PRIMARY KEY,
                                    name                  VARCHAR(150) NOT NULL,
                                    category_id            BIGINT NOT NULL REFERENCES catalog_food_categories(category_id),
                                    unit                   VARCHAR(20) NOT NULL,                  -- kg, litre, pack
                                    recommended_quantity    DECIMAL(10,2),
                                    description             VARCHAR(255)
);

CREATE TABLE catalog_nutrition_values (
                                          nutrition_value_id   BIGSERIAL PRIMARY KEY,
                                          food_item_id           BIGINT NOT NULL REFERENCES catalog_food_items(food_item_id),
                                          calories                DECIMAL(10,2),
                                          protein                 DECIMAL(10,2),
                                          carbs                   DECIMAL(10,2),
                                          fat                     DECIMAL(10,2),
                                          vitamins                 VARCHAR(150)
);

CREATE INDEX idx_catalog_items_category ON catalog_food_items(category_id);
CREATE INDEX idx_catalog_nutrition_item ON catalog_nutrition_values(food_item_id);


-- =========================================================
-- PROCUREMENT MANAGEMENT
-- =========================================================

CREATE TABLE procurement_suppliers (
                                       supplier_id       BIGSERIAL PRIMARY KEY,
                                       name               VARCHAR(150) NOT NULL,
                                       contact_person      VARCHAR(100),
                                       phone               VARCHAR(20),
                                       email               VARCHAR(150),
                                       address             VARCHAR(255),
                                       rating              DECIMAL(3,2)
);

CREATE TABLE procurement_requests (
                                      request_id          BIGSERIAL PRIMARY KEY,
                                      food_item_id          BIGINT NOT NULL REFERENCES catalog_food_items(food_item_id),
                                      requested_quantity      DECIMAL(10,2) NOT NULL,
                                      request_date              DATE NOT NULL DEFAULT CURRENT_DATE,
                                      requested_by                VARCHAR(100),
                                      status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING'  -- PENDING, APPROVED, REJECTED
);

CREATE TABLE procurement_purchase_orders (
                                             purchase_order_id    BIGSERIAL PRIMARY KEY,
                                             supplier_id            BIGINT NOT NULL REFERENCES procurement_suppliers(supplier_id),
                                             request_id              BIGINT NOT NULL REFERENCES procurement_requests(request_id),
                                             order_date                DATE NOT NULL DEFAULT CURRENT_DATE,
                                             expected_delivery_date    DATE,
                                             total_amount               DECIMAL(12,2),
                                             status                      VARCHAR(20) NOT NULL DEFAULT 'PLACED'   -- PLACED, DELIVERED, CANCELLED
);

CREATE TABLE procurement_order_items (
                                         order_item_id         BIGSERIAL PRIMARY KEY,
                                         purchase_order_id       BIGINT NOT NULL REFERENCES procurement_purchase_orders(purchase_order_id),
                                         food_item_id             BIGINT NOT NULL REFERENCES catalog_food_items(food_item_id),
                                         quantity                   DECIMAL(10,2) NOT NULL,
                                         unit_price                  DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_procurement_requests_item ON procurement_requests(food_item_id);
CREATE INDEX idx_purchase_orders_supplier ON procurement_purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_request ON procurement_purchase_orders(request_id);
CREATE INDEX idx_order_items_po ON procurement_order_items(purchase_order_id);
CREATE INDEX idx_order_items_item ON procurement_order_items(food_item_id);


-- =========================================================
-- INVENTORY & STOCK MANAGEMENT
-- =========================================================

CREATE TABLE inventory_warehouses (
                                      warehouse_id             BIGSERIAL PRIMARY KEY,
                                      name                       VARCHAR(150) NOT NULL,
                                      location                    VARCHAR(150),
                                      capacity                     DECIMAL(10,2),
                                      minimum_stock_threshold       DECIMAL(10,2)
);

CREATE TABLE inventory_stock_batches (
                                         batch_id              BIGSERIAL PRIMARY KEY,
                                         food_item_id            BIGINT NOT NULL REFERENCES catalog_food_items(food_item_id),
                                         batch_number              VARCHAR(50) NOT NULL,
                                         quantity_available          DECIMAL(10,2) NOT NULL,
                                         expiry_date                  DATE,
                                         received_date                 DATE NOT NULL DEFAULT CURRENT_DATE,
                                         warehouse_id                   BIGINT NOT NULL REFERENCES inventory_warehouses(warehouse_id)
);

CREATE TABLE inventory_stock_movements (
                                           movement_id            BIGSERIAL PRIMARY KEY,
                                           batch_id                  BIGINT NOT NULL REFERENCES inventory_stock_batches(batch_id),
                                           movement_type               VARCHAR(10) NOT NULL,              -- IN, OUT, TRANSFER
                                           quantity                      DECIMAL(10,2) NOT NULL,
                                           movement_date                  DATE NOT NULL DEFAULT CURRENT_DATE,
                                           reference_id                    BIGINT,                         -- e.g. distribution_plan_id
                                           remarks                          VARCHAR(255)
);

CREATE INDEX idx_stock_batches_item ON inventory_stock_batches(food_item_id);
CREATE INDEX idx_stock_batches_warehouse ON inventory_stock_batches(warehouse_id);
CREATE INDEX idx_stock_movements_batch ON inventory_stock_movements(batch_id);


-- =========================================================
-- FOOD DISTRIBUTION MANAGEMENT
-- =========================================================

CREATE TABLE distribution_centres (
                                      centre_id            BIGSERIAL PRIMARY KEY,
                                      name                   VARCHAR(150) NOT NULL,
                                      location                 VARCHAR(150),
                                      capacity                   DECIMAL(10,2),
                                      contact_person               VARCHAR(100)
);

CREATE TABLE distribution_plans (
                                    plan_id                 BIGSERIAL PRIMARY KEY,
                                    programme_id               BIGINT,                              -- FK added after nutrition_programmes exists
                                    planned_date                 DATE NOT NULL,
                                    target_beneficiary_count       INT,
                                    status                          VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, APPROVED, COMPLETED
                                    created_by                       VARCHAR(100)
);

CREATE TABLE distribution_plan_items (
                                         plan_item_id            BIGSERIAL PRIMARY KEY,
                                         plan_id                    BIGINT NOT NULL REFERENCES distribution_plans(plan_id),
                                         beneficiary_id                BIGINT NOT NULL REFERENCES beneficiary_profiles(beneficiary_id),
                                         food_item_id                    BIGINT NOT NULL REFERENCES catalog_food_items(food_item_id),
                                         planned_quantity                  DECIMAL(10,2) NOT NULL,
                                         centre_id                          BIGINT NOT NULL REFERENCES distribution_centres(centre_id)
);

CREATE TABLE distribution_status_log (
                                         status_log_id           BIGSERIAL PRIMARY KEY,
                                         plan_item_id                BIGINT NOT NULL REFERENCES distribution_plan_items(plan_item_id),
                                         status                         VARCHAR(20) NOT NULL,             -- PENDING, DISPATCHED, DELIVERED, SHORTAGE
                                         updated_date                     DATE NOT NULL DEFAULT CURRENT_DATE,
                                         updated_by                         VARCHAR(100),
                                         remarks                              VARCHAR(255)
);

CREATE INDEX idx_dist_plan_items_plan ON distribution_plan_items(plan_id);
CREATE INDEX idx_dist_plan_items_beneficiary ON distribution_plan_items(beneficiary_id);
CREATE INDEX idx_dist_plan_items_item ON distribution_plan_items(food_item_id);
CREATE INDEX idx_dist_status_log_item ON distribution_status_log(plan_item_id);


-- =========================================================
-- NUTRITION PROGRAMME MANAGEMENT
-- =========================================================

CREATE TABLE nutrition_programmes (
                                      programme_id          BIGSERIAL PRIMARY KEY,
                                      programme_name           VARCHAR(150) NOT NULL,
                                      description                VARCHAR(255),
                                      start_date                   DATE,
                                      end_date                       DATE,
                                      target_group                     VARCHAR(100),
                                      status                             VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'  -- ACTIVE, COMPLETED, CANCELLED
);

CREATE TABLE nutrition_meal_plans (
                                      meal_plan_id          BIGSERIAL PRIMARY KEY,
                                      programme_id              BIGINT NOT NULL REFERENCES nutrition_programmes(programme_id),
                                      meal_name                   VARCHAR(100) NOT NULL,
                                      frequency                     VARCHAR(20),                       -- daily, weekly
                                      dietary_notes                   VARCHAR(255)
);

CREATE TABLE nutrition_participation (
                                         participation_id       BIGSERIAL PRIMARY KEY,
                                         programme_id               BIGINT NOT NULL REFERENCES nutrition_programmes(programme_id),
                                         beneficiary_id                BIGINT NOT NULL REFERENCES beneficiary_profiles(beneficiary_id),
                                         enrolment_date                  DATE NOT NULL DEFAULT CURRENT_DATE,
                                         participation_status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'  -- ACTIVE, DROPPED, COMPLETED
);

CREATE INDEX idx_meal_plans_programme ON nutrition_meal_plans(programme_id);
CREATE INDEX idx_participation_programme ON nutrition_participation(programme_id);
CREATE INDEX idx_participation_beneficiary ON nutrition_participation(beneficiary_id);

-- Now that nutrition_programmes exists, wire up the FK deferred earlier
ALTER TABLE distribution_plans
    ADD CONSTRAINT fk_distribution_plans_programme
        FOREIGN KEY (programme_id) REFERENCES nutrition_programmes(programme_id);

CREATE INDEX idx_distribution_plans_programme ON distribution_plans(programme_id);


-- =========================================================
-- FOOD QUALITY & WASTAGE MANAGEMENT
-- =========================================================

CREATE TABLE quality_inspections (
                                     inspection_id          BIGSERIAL PRIMARY KEY,
                                     batch_id                  BIGINT NOT NULL REFERENCES inventory_stock_batches(batch_id),
                                     inspection_date              DATE NOT NULL DEFAULT CURRENT_DATE,
                                     inspector_name                 VARCHAR(100),
                                     quality_rating                   VARCHAR(20) NOT NULL,           -- PASS, FAIL, FLAGGED
                                     remarks                            VARCHAR(255)
);

CREATE TABLE quality_wastage_records (
                                         wastage_id             BIGSERIAL PRIMARY KEY,
                                         batch_id                  BIGINT NOT NULL REFERENCES inventory_stock_batches(batch_id),
                                         wastage_quantity             DECIMAL(10,2) NOT NULL,
                                         wastage_reason                 VARCHAR(50) NOT NULL,             -- EXPIRED, DAMAGED, SPOILED
                                         recorded_date                     DATE NOT NULL DEFAULT CURRENT_DATE,
                                         recorded_by                         VARCHAR(100)
);

CREATE TABLE quality_corrective_actions (
                                            action_id               BIGSERIAL PRIMARY KEY,
                                            wastage_id                 BIGINT NOT NULL REFERENCES quality_wastage_records(wastage_id),
                                            action_taken                  VARCHAR(255) NOT NULL,
                                            action_date                     DATE NOT NULL DEFAULT CURRENT_DATE,
                                            responsible_person                 VARCHAR(100),
                                            status                               VARCHAR(20) NOT NULL DEFAULT 'OPEN'  -- OPEN, RESOLVED
);

CREATE INDEX idx_inspections_batch ON quality_inspections(batch_id);
CREATE INDEX idx_wastage_batch ON quality_wastage_records(batch_id);
CREATE INDEX idx_corrective_wastage ON quality_corrective_actions(wastage_id);