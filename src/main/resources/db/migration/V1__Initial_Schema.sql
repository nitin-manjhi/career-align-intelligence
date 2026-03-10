-- 1. Initial Master Schema (Consolidated States, Cities, Users, Results, Requests, and Jobs)

-- States
CREATE TABLE IF NOT EXISTS states (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Cities
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL,
    CONSTRAINT fk_cities_state_id FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT uk_cities_name_state_id UNIQUE (name, state_id)
);
CREATE INDEX IF NOT EXISTS idx_cities_state_id ON cities(state_id);
CREATE INDEX IF NOT EXISTS idx_cities_name ON cities(name);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    role VARCHAR(50) DEFAULT 'USER',
    usage_limit INTEGER DEFAULT 2,
    analysis_count INTEGER DEFAULT 0,
    generation_count INTEGER DEFAULT 0,
    premium_active BOOLEAN DEFAULT FALSE,
    premium_usage_limit INT DEFAULT 0,
    premium_usage_count INT DEFAULT 0,
    generation_limit INTEGER DEFAULT 2,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users(deleted_at);

-- Analysis Result
CREATE TABLE IF NOT EXISTS analysis_result (
    id UUID PRIMARY KEY,
    resume_text TEXT,
    jd_text TEXT,
    company_name VARCHAR(255),
    score INTEGER,
    ai_response JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX IF NOT EXISTS idx_analysis_result_created_at ON analysis_result(created_at);

-- Upgrade Requests
CREATE TABLE IF NOT EXISTS upgrade_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_upgrade_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX IF NOT EXISTS idx_upgrade_requests_status ON upgrade_requests(status);

-- Analysis Job
CREATE TABLE IF NOT EXISTS analysis_job (
    id            UUID PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL, -- PENDING, PROCESSING, DONE, FAILED
    progress      INT DEFAULT 0,        -- 0 → 100
    result_id     UUID,                 -- FK to analysis_result.id
    model         VARCHAR(50),
    job_type      VARCHAR(30) DEFAULT 'RESUME_ANALYSIS',
    error_message TEXT,
    created_at    TIMESTAMP   NOT NULL,
    completed_at  TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_analysis_job_user_id ON analysis_job (user_id);
CREATE INDEX IF NOT EXISTS idx_analysis_job_status ON analysis_job (status);
