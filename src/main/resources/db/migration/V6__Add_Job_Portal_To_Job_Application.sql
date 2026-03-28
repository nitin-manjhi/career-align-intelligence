-- Add job_portal column to job_application table
ALTER TABLE job_application 
ADD COLUMN job_portal VARCHAR(255);
