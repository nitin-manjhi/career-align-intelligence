ALTER TABLE analysis_job ADD COLUMN job_type VARCHAR(30);
UPDATE analysis_job SET job_type = 'RESUME_ANALYSIS' WHERE job_type IS NULL;
