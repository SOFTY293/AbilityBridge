-- =============================================================
-- AbilityBridge — V2 Seed Data
-- =============================================================

-- ── Seed skills ───────────────────────────────────────────────
INSERT INTO skills (id, name, category) VALUES
  (uuid_generate_v4(), 'JavaScript',         'Programming'),
  (uuid_generate_v4(), 'Python',             'Programming'),
  (uuid_generate_v4(), 'Java',               'Programming'),
  (uuid_generate_v4(), 'SQL',                'Data'),
  (uuid_generate_v4(), 'Data Analysis',      'Data'),
  (uuid_generate_v4(), 'Machine Learning',   'Data'),
  (uuid_generate_v4(), 'Project Management', 'Management'),
  (uuid_generate_v4(), 'Communication',      'Soft Skills'),
  (uuid_generate_v4(), 'Customer Service',   'Soft Skills'),
  (uuid_generate_v4(), 'Graphic Design',     'Design'),
  (uuid_generate_v4(), 'UX Design',          'Design'),
  (uuid_generate_v4(), 'Content Writing',    'Marketing'),
  (uuid_generate_v4(), 'Social Media',       'Marketing'),
  (uuid_generate_v4(), 'Accounting',         'Finance'),
  (uuid_generate_v4(), 'Microsoft Excel',    'Tools'),
  (uuid_generate_v4(), 'React',              'Programming'),
  (uuid_generate_v4(), 'Spring Boot',        'Programming'),
  (uuid_generate_v4(), 'Docker',             'DevOps'),
  (uuid_generate_v4(), 'AWS',                'Cloud'),
  (uuid_generate_v4(), 'Technical Writing',  'Soft Skills')
ON CONFLICT (name) DO NOTHING;

-- ── Seed free courses ──────────────────────────────────────────
INSERT INTO courses (id, title, provider, url, is_free, duration_hours)
SELECT uuid_generate_v4(), 'Python for Everybody',        'Coursera', 'https://coursera.org/specializations/python',  true, 30
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Python for Everybody');

INSERT INTO courses (id, title, provider, url, is_free, duration_hours)
SELECT uuid_generate_v4(), 'SQL for Data Science',        'Coursera', 'https://coursera.org/learn/sql-for-data-science', true, 12
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'SQL for Data Science');

INSERT INTO courses (id, title, provider, url, is_free, duration_hours)
SELECT uuid_generate_v4(), 'AWS Cloud Practitioner',      'AWS',      'https://aws.amazon.com/training/digital',      true, 15
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'AWS Cloud Practitioner');

INSERT INTO courses (id, title, provider, url, is_free, duration_hours)
SELECT uuid_generate_v4(), 'UX Design Fundamentals',      'Google',   'https://grow.google/certificates/ux-design',  true, 40
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'UX Design Fundamentals');

INSERT INTO courses (id, title, provider, url, is_free, duration_hours)
SELECT uuid_generate_v4(), 'Project Management Basics',   'Google',   'https://grow.google/certificates/project-management', true, 40
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Project Management Basics');

-- ── Seed admin user ────────────────────────────────────────────
-- Password: Admin@123! (BCrypt hash — CHANGE IN PRODUCTION)
INSERT INTO users (id, email, password_hash, role, status, email_verified)
VALUES (
  uuid_generate_v4(),
  'admin@abilitybridge.io',
  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewmT9xBrRhCkzJwG',
  'ADMIN',
  'ACTIVE',
  true
) ON CONFLICT (email) DO NOTHING;
