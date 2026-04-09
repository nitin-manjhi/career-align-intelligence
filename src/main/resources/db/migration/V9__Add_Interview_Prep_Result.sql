-- Interview Prep Results table
CREATE TABLE interview_prep_result (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(255),
    role VARCHAR(255),
    experience VARCHAR(100),
    domain VARCHAR(255),
    type VARCHAR(30) NOT NULL, -- TOPIC_WISE or SCENARIO_BASED
    model VARCHAR(50),
    generated_content TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_prep_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_interview_prep_user_id ON interview_prep_result(user_id);
CREATE INDEX idx_interview_prep_created_at ON interview_prep_result(created_at);
