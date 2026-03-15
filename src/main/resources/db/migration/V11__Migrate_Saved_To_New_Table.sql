-- Move existing SAVED jobs to the new table
INSERT INTO saved_job (user_id, company_name, job_title, location, salary, skills, job_description, apply_link, original_posted_date, created_at)
SELECT user_id, company_name, job_title, location, salary, skills, job_description, apply_link, original_posted_date, created_at
FROM job_application
WHERE status = 'SAVED';

-- Clean up the old table
DELETE FROM job_application WHERE status = 'SAVED';
