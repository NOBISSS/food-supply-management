CREATE TABLE stock_batches (
    id BIGSERIAL PRIMARY KEY,
    food_item_id BIGINT NOT NULL,
    batch_no VARCHAR(50) NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    received_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date DATE,
    min_stock_level NUMERIC(10,2) NOT NULL DEFAULT 0,
    warehouse_location VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_stock_batches_food_item_id ON stock_batches(food_item_id);
CREATE INDEX idx_stock_batches_expiry_date ON stock_batches(expiry_date);
CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    stock_batch_id BIGINT NOT NULL REFERENCES stock_batches(id),
    movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
    quantity NUMERIC(10,2) NOT NULL,
    reason VARCHAR(100),
    moved_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_stock_movements_stock_batch_id ON stock_movements(stock_batch_id);
