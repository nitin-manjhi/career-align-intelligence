-- Drop the old unique constraint on cities.name if it exists
ALTER TABLE cities DROP CONSTRAINT IF EXISTS ukl61tawv0e2a93es77jkyvi7qa;

-- Add the new composite unique constraint
ALTER TABLE cities ADD CONSTRAINT uk_cities_name_state_id UNIQUE (name, state_id);

