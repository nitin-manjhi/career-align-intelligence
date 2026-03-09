-- DANGER: This script will drop ALL tables in the 'public' schema.
-- Run this to completely reset your database and allow Flyway to re-run migrations.

DO $$ 
DECLARE 
    r RECORD;
BEGIN
    -- Iterate through all tables in the public schema and drop them
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
END $$;

-- Optional: Reset the vector extension if needed (usually stays persistent)
-- CREATE EXTENSION IF NOT EXISTS vector;
