-- Insert Admin User
INSERT INTO users (username, password, name, email, role, usage_limit, analysis_count, generation_count, created_at, updated_at)
VALUES (
    'admin', 
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', -- password: admin123
    'Administrator', 
    'admin@example.com', 
    'ADMIN', 
    999999, 
    0, 
    0, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;
