-- Add enabled column to users table
ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT false;

-- Auto-approve existing users so we don't lock them out
UPDATE users SET enabled = true;
