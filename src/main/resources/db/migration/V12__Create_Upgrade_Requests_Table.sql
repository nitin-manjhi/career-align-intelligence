-- Create upgrade_requests table
CREATE TABLE IF NOT EXISTS upgrade_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_upgrade_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create index for status
CREATE INDEX IF NOT EXISTS idx_upgrade_requests_status ON upgrade_requests(status);
