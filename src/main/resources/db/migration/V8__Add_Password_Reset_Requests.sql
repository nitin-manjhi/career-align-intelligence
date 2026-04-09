-- V8: Add Password Reset Requests table for Admin Approval workflow
CREATE TABLE password_reset_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    new_password VARCHAR(255) NOT NULL,
    requested_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_password_reset_requests_user_id ON password_reset_requests(user_id);
