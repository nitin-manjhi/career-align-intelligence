-- Drop the vector_store table as we are moving to single-pass LLM analysis
DROP TABLE IF EXISTS vector_store;

-- Optionally remove the extension if no other tables use it
-- DROP EXTENSION IF EXISTS vector; 
-- Keeping extension for now just in case, but removing the data-heavy table.
