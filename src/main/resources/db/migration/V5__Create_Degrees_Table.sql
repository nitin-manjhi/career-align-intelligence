-- Create degrees table
CREATE TABLE IF NOT EXISTS degrees (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_degrees_name ON degrees(name);

