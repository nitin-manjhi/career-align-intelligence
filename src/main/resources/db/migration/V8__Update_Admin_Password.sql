-- Update Admin User Password to 'admin123'
UPDATE users 
SET password = '$2a$10$OTLAVfGvPgE/lbJOHShxK.LatpNWdZCAEJsEz8CD.lWBxn3YozM6a' 
WHERE username = 'admin';
