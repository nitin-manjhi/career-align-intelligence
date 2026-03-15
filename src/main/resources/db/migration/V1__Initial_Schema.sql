-- Consolidated Core Schema (Users, Jobs, Results, Tracking)

-- 1. Locations
CREATE TABLE states (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cities (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL,
    CONSTRAINT fk_cities_state_id FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT uk_cities_name_state_id UNIQUE (name, state_id)
);
CREATE INDEX idx_cities_state_id ON cities(state_id);
CREATE INDEX idx_cities_name ON cities(name);

-- 2. Users
CREATE TABLE users (
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
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);

-- 3. Analysis Result (ATS Data)
CREATE TABLE analysis_result (
    id UUID PRIMARY KEY,
    resume_text TEXT,
    jd_text TEXT,
    company_name VARCHAR(255),
    score INTEGER,
    ai_response JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_analysis_result_created_at ON analysis_result(created_at);

-- 4. Analysis Jobs (Async Processing)
CREATE TABLE analysis_job (
    id            UUID PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL, -- PENDING, PROCESSING, DONE, FAILED
    progress      INT DEFAULT 0,        -- 0 → 100
    result_id     UUID,                 -- FK to analysis_result.id
    model         VARCHAR(50),
    job_type      VARCHAR(30) DEFAULT 'RESUME_ANALYSIS',
    job_data      TEXT,
    error_message TEXT,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP
);
CREATE INDEX idx_analysis_job_user_id ON analysis_job (user_id);
CREATE INDEX idx_analysis_job_status ON analysis_job (status);

-- 5. Job Application Tracking
CREATE TABLE job_application (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    analysis_id UUID,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255),
    location VARCHAR(255),
    salary VARCHAR(255),
    skills TEXT,
    job_description TEXT,
    apply_link VARCHAR(1000),
    original_posted_date VARCHAR(255),
    status VARCHAR(20) NOT NULL, -- e.g. INITIALIZED, APPLIED, INTERVIEWING
    hr_name VARCHAR(255),
    hr_email VARCHAR(255),
    phone VARCHAR(50),
    resume_path VARCHAR(255),
    closing_date DATE,
    applied_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_application_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_job_application_user_id ON job_application(user_id);

-- 6. Saved Jobs (Watchlist)
CREATE TABLE saved_job (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255),
    location VARCHAR(255),
    salary VARCHAR(255),
    skills TEXT,
    job_description TEXT,
    apply_link VARCHAR(1000),
    original_posted_date VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_job_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_saved_job_user_id ON saved_job(user_id);

-- 7. Governance
CREATE TABLE upgrade_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_upgrade_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_upgrade_requests_status ON upgrade_requests(status);
