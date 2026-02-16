-- Add email column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255);

-- Create index for email to improve findByEmail performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Add unique constraint to email to avoid duplicates
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
