-- =============================================================
-- AbilityBridge — V1 Initial Schema
-- =============================================================

-- ── Extensions ────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- for fuzzy job search

-- =============================================================
-- FR1 · USER MANAGEMENT
-- =============================================================

CREATE TYPE user_role AS ENUM ('SEEKER', 'EMPLOYER', 'MENTOR', 'ADMIN');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'PENDING_VERIFICATION', 'SUSPENDED', 'REMOVED');
CREATE TYPE auth_provider AS ENUM ('LOCAL', 'GOOGLE', 'FACEBOOK', 'APPLE');

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) UNIQUE,
    phone           VARCHAR(30)  UNIQUE,
    password_hash   VARCHAR(255),
    role            user_role        NOT NULL,
    provider        auth_provider    NOT NULL DEFAULT 'LOCAL',
    provider_id     VARCHAR(255),
    status          account_status   NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified  BOOLEAN          NOT NULL DEFAULT FALSE,
    phone_verified  BOOLEAN          NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP        NOT NULL DEFAULT NOW(),
    last_login_at   TIMESTAMP,
    CONSTRAINT chk_contact CHECK (email IS NOT NULL OR phone IS NOT NULL)
);

CREATE TABLE accessibility_settings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    screen_reader   BOOLEAN NOT NULL DEFAULT FALSE,
    high_contrast   BOOLEAN NOT NULL DEFAULT FALSE,
    voice_nav       BOOLEAN NOT NULL DEFAULT FALSE,
    font_size       VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',  -- SMALL | MEDIUM | LARGE | XLARGE
    dyslexia_font   BOOLEAN NOT NULL DEFAULT FALSE,
    sign_lang_video BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id)
);

-- ── Refresh Tokens ─────────────────────────────────────────────
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================================
-- FR1 · PROFILES (separated from users for privacy — NFR3)
-- =============================================================

CREATE TABLE seeker_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    full_name           VARCHAR(255) NOT NULL,
    headline            VARCHAR(255),
    bio                 TEXT,
    location            VARCHAR(255),
    profile_picture_url VARCHAR(512),
    cv_url              VARCHAR(512),
    video_profile_url   VARCHAR(512),
    voice_profile_url   VARCHAR(512),
    anonymous_mode      BOOLEAN NOT NULL DEFAULT FALSE,
    target_job_category VARCHAR(100),
    availability        VARCHAR(50),  -- FULL_TIME | PART_TIME | FREELANCE | MICRO_TASKS
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Disability info stored separately (NFR3 — personal data isolation)
CREATE TABLE seeker_disability_info (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    disability_type     VARCHAR(255),
    disclosure_level    VARCHAR(20) NOT NULL DEFAULT 'PRIVATE', -- PRIVATE | EMPLOYERS_ONLY | PUBLIC
    support_needs       TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE employer_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    company_name        VARCHAR(255) NOT NULL,
    company_logo_url    VARCHAR(512),
    industry            VARCHAR(100),
    company_size        VARCHAR(50),
    website             VARCHAR(255),
    description         TEXT,
    location            VARCHAR(255),
    is_verified         BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at         TIMESTAMP,
    verified_by         UUID REFERENCES users(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================================
-- FR2 · SKILLS & LEARNING
-- =============================================================

CREATE TABLE skills (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    category    VARCHAR(100),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE seeker_skills (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id        UUID NOT NULL REFERENCES skills(id),
    proficiency     VARCHAR(20) NOT NULL DEFAULT 'BEGINNER', -- BEGINNER|INTERMEDIATE|ADVANCED|EXPERT
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, skill_id)
);

CREATE TABLE courses (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title           VARCHAR(255) NOT NULL,
    provider        VARCHAR(100),
    url             VARCHAR(512) NOT NULL,
    skill_id        UUID REFERENCES skills(id),
    is_free         BOOLEAN NOT NULL DEFAULT TRUE,
    duration_hours  INTEGER,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE skill_badges (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id        UUID NOT NULL REFERENCES skills(id),
    course_id       UUID REFERENCES courses(id),
    awarded_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, skill_id)
);

CREATE TABLE portfolio_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    item_type       VARCHAR(30) NOT NULL, -- PROJECT | TASK | BADGE | CERTIFICATE
    url             VARCHAR(512),
    reference_id    UUID,  -- task_id or badge_id
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================================
-- FR3 · MICRO TASK MARKETPLACE
-- =============================================================

CREATE TYPE task_status AS ENUM ('OPEN','IN_PROGRESS','SUBMITTED','APPROVED','REJECTED','CANCELLED');
CREATE TYPE payment_status AS ENUM ('PENDING','PROCESSING','COMPLETED','FAILED','REFUNDED');

CREATE TABLE micro_tasks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    poster_id       UUID NOT NULL REFERENCES users(id),
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    category        VARCHAR(100),
    pay_rate        NUMERIC(10,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'USD',
    requirements    TEXT,
    deadline        TIMESTAMP,
    status          task_status NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE task_applications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id         UUID NOT NULL REFERENCES micro_tasks(id),
    applicant_id    UUID NOT NULL REFERENCES users(id),
    cover_note      TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING|ACCEPTED|REJECTED
    applied_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(task_id, applicant_id)
);

CREATE TABLE task_completions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id             UUID NOT NULL REFERENCES micro_tasks(id),
    worker_id           UUID NOT NULL REFERENCES users(id),
    submission_url      VARCHAR(512),
    submission_notes    TEXT,
    submitted_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    approved_at         TIMESTAMP,
    payment_status      payment_status NOT NULL DEFAULT 'PENDING',
    payment_reference   VARCHAR(255)
);

CREATE TABLE task_ratings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id         UUID NOT NULL REFERENCES micro_tasks(id),
    rater_id        UUID NOT NULL REFERENCES users(id),
    ratee_id        UUID NOT NULL REFERENCES users(id),
    score           SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment         TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(task_id, rater_id, ratee_id)
);

-- =============================================================
-- FR4 · JOB BOARD
-- =============================================================

CREATE TYPE job_type AS ENUM ('FULL_TIME','PART_TIME','CONTRACT','INTERNSHIP','VOLUNTEER');
CREATE TYPE job_status AS ENUM ('DRAFT','ACTIVE','PAUSED','CLOSED');
CREATE TYPE application_status AS ENUM ('SUBMITTED','UNDER_REVIEW','SHORTLISTED','INTERVIEW','OFFERED','REJECTED','WITHDRAWN');
CREATE TYPE apply_format AS ENUM ('CV','VIDEO','VOICE','PORTFOLIO');

CREATE TABLE job_listings (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employer_id             UUID NOT NULL REFERENCES users(id),
    title                   VARCHAR(255) NOT NULL,
    description             TEXT NOT NULL,
    job_type                job_type NOT NULL,
    location                VARCHAR(255),
    is_remote               BOOLEAN NOT NULL DEFAULT FALSE,
    salary_min              NUMERIC(12,2),
    salary_max              NUMERIC(12,2),
    currency                VARCHAR(3) NOT NULL DEFAULT 'USD',
    status                  job_status NOT NULL DEFAULT 'DRAFT',
    application_deadline    TIMESTAMP,
    accommodations_offered  TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE job_required_skills (
    job_id      UUID NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
    skill_id    UUID NOT NULL REFERENCES skills(id),
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (job_id, skill_id)
);

CREATE TABLE job_applications (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id              UUID NOT NULL REFERENCES job_listings(id),
    applicant_id        UUID NOT NULL REFERENCES users(id),
    apply_format        apply_format NOT NULL DEFAULT 'CV',
    submission_url      VARCHAR(512),
    cover_letter        TEXT,
    status              application_status NOT NULL DEFAULT 'SUBMITTED',
    anonymous_mode      BOOLEAN NOT NULL DEFAULT FALSE,
    match_score         NUMERIC(5,2),
    applied_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(job_id, applicant_id)
);

-- Smart match scores cache
CREATE TABLE job_match_scores (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id      UUID NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
    score       NUMERIC(5,2) NOT NULL,
    computed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, job_id)
);

-- =============================================================
-- FR5 · ACCOMMODATION NEGOTIATION
-- =============================================================

CREATE TYPE negotiation_status AS ENUM ('OPEN','IN_PROGRESS','AGREED','DECLINED','EXPIRED');

CREATE TABLE accommodation_needs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    need_type       VARCHAR(100) NOT NULL,
    description     TEXT,
    is_mandatory    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE accommodation_negotiations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id          UUID REFERENCES job_listings(id),
    seeker_id       UUID NOT NULL REFERENCES users(id),
    employer_id     UUID NOT NULL REFERENCES users(id),
    status          negotiation_status NOT NULL DEFAULT 'OPEN',
    compatibility   NUMERIC(5,2),
    opened_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMP
);

CREATE TABLE accommodation_messages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    negotiation_id      UUID NOT NULL REFERENCES accommodation_negotiations(id) ON DELETE CASCADE,
    sender_id           UUID NOT NULL REFERENCES users(id),
    content             TEXT NOT NULL,
    sent_at             TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE accommodation_agreements (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    negotiation_id      UUID NOT NULL REFERENCES accommodation_negotiations(id),
    agreed_terms        TEXT NOT NULL,
    agreed_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    seeker_signed       BOOLEAN NOT NULL DEFAULT FALSE,
    employer_signed     BOOLEAN NOT NULL DEFAULT FALSE
);

-- =============================================================
-- FR6 · EMPLOYER ACCOUNTABILITY
-- =============================================================

CREATE TYPE badge_tier AS ENUM ('BRONZE','SILVER','GOLD');

CREATE TABLE workplace_reality_scores (
    id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employer_id                 UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    overall_score               NUMERIC(4,2) NOT NULL DEFAULT 0,
    hiring_score                NUMERIC(4,2) NOT NULL DEFAULT 0,
    accommodation_score         NUMERIC(4,2) NOT NULL DEFAULT 0,
    community_score             NUMERIC(4,2) NOT NULL DEFAULT 0,
    total_hires                 INTEGER NOT NULL DEFAULT 0,
    accommodation_fulfil_rate   NUMERIC(5,2) NOT NULL DEFAULT 0,
    computed_at                 TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE inclusivity_badges (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employer_id     UUID NOT NULL REFERENCES users(id),
    tier            badge_tier NOT NULL,
    awarded_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    awarded_by      UUID NOT NULL REFERENCES users(id),
    valid_until     TIMESTAMP,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employer_ratings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employer_id     UUID NOT NULL REFERENCES users(id),
    rater_id        UUID NOT NULL REFERENCES users(id),
    score           SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment         TEXT,
    is_anonymous    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================================
-- FR7 · MENTORSHIP NETWORK
-- =============================================================

CREATE TYPE mentorship_status AS ENUM ('PENDING','ACTIVE','COMPLETED','DECLINED','CANCELLED');

CREATE TABLE mentor_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    industry            VARCHAR(100),
    disability_type     VARCHAR(255),
    career_stage        VARCHAR(50),
    bio                 TEXT,
    offers_cv_review    BOOLEAN NOT NULL DEFAULT FALSE,
    offers_interview    BOOLEAN NOT NULL DEFAULT FALSE,
    offers_career_adv   BOOLEAN NOT NULL DEFAULT FALSE,
    max_mentees         INTEGER NOT NULL DEFAULT 3,
    is_available        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE mentorship_requests (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mentor_id       UUID NOT NULL REFERENCES users(id),
    mentee_id       UUID NOT NULL REFERENCES users(id),
    message         TEXT,
    status          mentorship_status NOT NULL DEFAULT 'PENDING',
    requested_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    responded_at    TIMESTAMP,
    UNIQUE(mentor_id, mentee_id)
);

-- =============================================================
-- FR8 · MESSAGING & NOTIFICATIONS
-- =============================================================

CREATE TYPE conversation_type AS ENUM ('DIRECT','NEGOTIATION','MENTORSHIP','SUPPORT');
CREATE TYPE notification_type AS ENUM (
    'APPLICATION_UPDATE','TASK_ASSIGNED','MESSAGE_RECEIVED',
    'MATCH_FOUND','BADGE_AWARDED','REPORT_UPDATE','INTERVIEW_SCHEDULED'
);

CREATE TABLE conversations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type            conversation_type NOT NULL DEFAULT 'DIRECT',
    reference_id    UUID,  -- nullable job/task/negotiation id
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE conversation_participants (
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE messages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id     UUID NOT NULL REFERENCES conversations(id),
    sender_id           UUID NOT NULL REFERENCES users(id),
    content             TEXT,
    message_format      VARCHAR(10) NOT NULL DEFAULT 'TEXT', -- TEXT | VOICE | FILE
    media_url           VARCHAR(512),
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at             TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE interview_schedules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id          UUID NOT NULL REFERENCES job_listings(id),
    applicant_id    UUID NOT NULL REFERENCES users(id),
    employer_id     UUID NOT NULL REFERENCES users(id),
    scheduled_at    TIMESTAMP NOT NULL,
    duration_mins   INTEGER NOT NULL DEFAULT 60,
    format          VARCHAR(30),  -- VIDEO | PHONE | IN_PERSON
    meeting_url     VARCHAR(512),
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            notification_type NOT NULL,
    title           VARCHAR(255) NOT NULL,
    body            TEXT,
    reference_id    UUID,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================================
-- FR9 · DISCRIMINATION REPORTING
-- =============================================================

CREATE TYPE report_status AS ENUM ('SUBMITTED','UNDER_REVIEW','ACTIONED','DISMISSED');

CREATE TABLE discrimination_reports (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id     UUID NOT NULL REFERENCES users(id),
    employer_id     UUID NOT NULL REFERENCES users(id),
    description     TEXT NOT NULL,
    evidence_url    VARCHAR(512),
    is_anonymous    BOOLEAN NOT NULL DEFAULT TRUE,
    status          report_status NOT NULL DEFAULT 'SUBMITTED',
    submitted_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    reviewed_at     TIMESTAMP,
    reviewed_by     UUID REFERENCES users(id)
);

CREATE TABLE employer_flags (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employer_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    flagged_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    flagged_by      UUID NOT NULL REFERENCES users(id),
    reason          TEXT,
    is_resolved     BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at     TIMESTAMP,
    admin_notes     TEXT
);

-- =============================================================
-- INDEXES — Performance (NFR2)
-- =============================================================

CREATE INDEX idx_users_email         ON users(email);
CREATE INDEX idx_users_role          ON users(role);
CREATE INDEX idx_users_status        ON users(status);
CREATE INDEX idx_job_listings_status ON job_listings(status);
CREATE INDEX idx_job_listings_remote ON job_listings(is_remote);
CREATE INDEX idx_job_listings_title  ON job_listings USING gin(to_tsvector('english', title || ' ' || description));
CREATE INDEX idx_micro_tasks_status  ON micro_tasks(status);
CREATE INDEX idx_messages_conv       ON messages(conversation_id, sent_at DESC);
CREATE INDEX idx_notifications_user  ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_reports_employer    ON discrimination_reports(employer_id, status);
CREATE INDEX idx_match_scores        ON job_match_scores(user_id, score DESC);
CREATE INDEX idx_seeker_skills       ON seeker_skills(user_id);
CREATE INDEX idx_job_req_skills      ON job_required_skills(job_id);
