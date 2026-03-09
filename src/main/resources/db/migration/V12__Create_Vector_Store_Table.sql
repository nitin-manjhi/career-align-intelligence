-- Database Reset & Single Provider Setup (Ollama Only)

-- 1. Drop old multi-provider tables if they exist
DROP TABLE IF EXISTS vector_store_ollama;
DROP TABLE IF EXISTS vector_store_openai;
DROP TABLE IF EXISTS vector_store_gemini;
DROP TABLE IF EXISTS vector_store;

-- 2. Create the unified vector_store table (1024 dimensions for Ollama BGE-M3)
CREATE TABLE vector_store (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1024)
);

-- 3. Create optimized index
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding ON vector_store USING hnsw (embedding vector_cosine_ops);
