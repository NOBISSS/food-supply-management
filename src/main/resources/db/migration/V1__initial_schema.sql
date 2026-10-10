CREATE TABLE test_ping (
    id SERIAL PRIMARY KEY,
    message VARCHAR(100) NOT NULL DEFAULT 'migration works',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);