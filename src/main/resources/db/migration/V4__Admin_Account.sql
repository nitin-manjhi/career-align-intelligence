-- Insert Admin User
INSERT INTO users (username, password, name, email, role, usage_limit, analysis_count, generation_count, premium_active, created_at, updated_at)
VALUES (
    'admin', 
    '$2a$10$OTLAVfGvPgE/lbJOHShxK.LatpNWdZCAEJsEz8CD.lWBxn3YozM6a',
    'Administrator', 
    'admin@example.com', 
    'ADMIN', 
    999999, 
    0, 
    0,
    TRUE,
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;
