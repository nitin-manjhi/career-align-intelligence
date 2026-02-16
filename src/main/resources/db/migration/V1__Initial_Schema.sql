-- Create states table
CREATE TABLE IF NOT EXISTS states (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create cities table
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL,
    CONSTRAINT fk_cities_state_id FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT uk_cities_name_state_id UNIQUE (name, state_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_cities_state_id ON cities(state_id);
CREATE INDEX IF NOT EXISTS idx_cities_name ON cities(name);

