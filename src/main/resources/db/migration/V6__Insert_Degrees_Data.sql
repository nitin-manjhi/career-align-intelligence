-- degree table
INSERT INTO degrees (id, name) VALUES
                                   (1,'Bachelor of Technology (B.Tech)'),
                                   (2,'Bachelor of Engineering (B.E)'),
                                   (3,'Bachelor of Science (B.Sc)'),
                                   (4,'Bachelor of Computer Applications (BCA)'),
                                   (5,'Bachelor of Commerce (B.Com)'),
                                   (6,'Bachelor of Arts (B.A)'),
                                   (7,'Master of Technology (M.Tech)'),
                                   (8,'Master of Engineering (M.E)'),
                                   (9,'Master of Science (M.Sc)'),
                                   (10,'Master of Computer Applications (MCA)'),
                                   (11,'Master of Business Administration (MBA)'),
                                   (12,'Master of Commerce (M.Com)'),
                                   (13,'Doctor of Philosophy (PhD)'),
                                   (14,'Diploma in Engineering'),
                                   (15,'Post Graduate Diploma')
    ON CONFLICT (id) DO NOTHING;