-- Create colleges table
CREATE TABLE IF NOT EXISTS colleges (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL,
    CONSTRAINT fk_colleges_state_id FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT uk_colleges_name_state_id UNIQUE (name, state_id)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_colleges_state_id ON colleges(state_id);

