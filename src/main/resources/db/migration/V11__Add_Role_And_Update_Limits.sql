-- Add role column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'USER';

-- Update usage_limit default for future rows
ALTER TABLE users ALTER COLUMN usage_limit SET DEFAULT 2;

-- Update existing users to have the new limit of 2 if they were at 10
UPDATE users SET usage_limit = 2 WHERE usage_limit = 10;
