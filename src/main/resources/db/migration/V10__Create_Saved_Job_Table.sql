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
