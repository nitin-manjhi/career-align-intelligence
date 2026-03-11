-- Enable the pgvector extension to support vector data types and similarity search
CREATE EXTENSION IF NOT EXISTS vector;

-- Create the unified vector_store table (1024 dimensions for Ollama BGE-M3)
CREATE TABLE vector_store (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1024)
);

-- Create optimized index
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding ON vector_store USING hnsw (embedding vector_cosine_ops);
