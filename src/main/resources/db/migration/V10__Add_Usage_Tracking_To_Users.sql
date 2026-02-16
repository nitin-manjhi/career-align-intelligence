-- Add usage tracking columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS usage_limit INTEGER DEFAULT 10;
ALTER TABLE users ADD COLUMN IF NOT EXISTS analysis_count INTEGER DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS generation_count INTEGER DEFAULT 0;
