CREATE TABLE job_application (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_description TEXT,
    status VARCHAR(20) NOT NULL,
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
