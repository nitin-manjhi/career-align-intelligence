-- Create analysis_result table
CREATE TABLE IF NOT EXISTS analysis_result (
    id UUID PRIMARY KEY,
    resume_text TEXT,
    jd_text TEXT,
    score INTEGER,
    ai_response JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_analysis_result_id ON analysis_result(id);
CREATE INDEX IF NOT EXISTS idx_analysis_result_created_at ON analysis_result(created_at);

