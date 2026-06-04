--
-- PostgreSQL database dump
--

\restrict LseBOVWRmSPf2QB7cR4fZvkbFplxuw4afHFFq9Io06F0H39qEcAC39DDLLjVNqe

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-04-30 23:36:26

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 7 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: abilitybridge
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO abilitybridge;

--
-- TOC entry 3 (class 3079 OID 16418)
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- TOC entry 5495 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- TOC entry 2 (class 3079 OID 16407)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- TOC entry 5496 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 223 (class 1259 OID 16556)
-- Name: accessibility_settings; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.accessibility_settings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    screen_reader boolean DEFAULT false NOT NULL,
    high_contrast boolean DEFAULT false NOT NULL,
    voice_nav boolean DEFAULT false NOT NULL,
    font_size character varying(10) DEFAULT 'MEDIUM'::character varying NOT NULL,
    dyslexia_font boolean DEFAULT false NOT NULL,
    sign_lang_video boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.accessibility_settings OWNER TO abilitybridge;

--
-- TOC entry 244 (class 1259 OID 17165)
-- Name: accommodation_agreements; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.accommodation_agreements (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    negotiation_id uuid NOT NULL,
    agreed_terms text NOT NULL,
    agreed_at timestamp without time zone DEFAULT now() NOT NULL,
    seeker_signed boolean DEFAULT false NOT NULL,
    employer_signed boolean DEFAULT false NOT NULL
);


ALTER TABLE public.accommodation_agreements OWNER TO abilitybridge;

--
-- TOC entry 243 (class 1259 OID 17141)
-- Name: accommodation_messages; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.accommodation_messages (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    negotiation_id uuid NOT NULL,
    sender_id uuid NOT NULL,
    content text NOT NULL,
    sent_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.accommodation_messages OWNER TO abilitybridge;

--
-- TOC entry 241 (class 1259 OID 17093)
-- Name: accommodation_needs; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.accommodation_needs (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    need_type character varying(100) NOT NULL,
    description text,
    is_mandatory boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.accommodation_needs OWNER TO abilitybridge;

--
-- TOC entry 242 (class 1259 OID 17113)
-- Name: accommodation_negotiations; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.accommodation_negotiations (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    job_id uuid,
    seeker_id uuid NOT NULL,
    employer_id uuid NOT NULL,
    status character varying(30) NOT NULL,
    compatibility numeric(5,2),
    opened_at timestamp without time zone DEFAULT now() NOT NULL,
    resolved_at timestamp without time zone
);


ALTER TABLE public.accommodation_negotiations OWNER TO abilitybridge;

--
-- TOC entry 251 (class 1259 OID 17382)
-- Name: conversation_participants; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.conversation_participants (
    conversation_id uuid NOT NULL,
    user_id uuid NOT NULL,
    joined_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.conversation_participants OWNER TO abilitybridge;

--
-- TOC entry 250 (class 1259 OID 17371)
-- Name: conversations; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.conversations (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    reference_id uuid,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    type character varying(255)
);


ALTER TABLE public.conversations OWNER TO abilitybridge;

--
-- TOC entry 230 (class 1259 OID 16725)
-- Name: courses; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.courses (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    title character varying(255) NOT NULL,
    provider character varying(100),
    url character varying(512) NOT NULL,
    skill_id uuid,
    is_free boolean DEFAULT true NOT NULL,
    duration_hours integer,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.courses OWNER TO abilitybridge;

--
-- TOC entry 255 (class 1259 OID 17491)
-- Name: discrimination_reports; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.discrimination_reports (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    reporter_id uuid NOT NULL,
    employer_id uuid NOT NULL,
    description text NOT NULL,
    evidence_url character varying(512),
    is_anonymous boolean DEFAULT true NOT NULL,
    status character varying(30) NOT NULL,
    submitted_at timestamp without time zone DEFAULT now() NOT NULL,
    reviewed_at timestamp without time zone,
    reviewed_by uuid
);


ALTER TABLE public.discrimination_reports OWNER TO abilitybridge;

--
-- TOC entry 256 (class 1259 OID 17524)
-- Name: employer_flags; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.employer_flags (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    employer_id uuid NOT NULL,
    flagged_at timestamp without time zone DEFAULT now() NOT NULL,
    flagged_by uuid NOT NULL,
    reason text,
    is_resolved boolean DEFAULT false NOT NULL,
    resolved_at timestamp without time zone,
    admin_notes text
);


ALTER TABLE public.employer_flags OWNER TO abilitybridge;

--
-- TOC entry 227 (class 1259 OID 16657)
-- Name: employer_profiles; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.employer_profiles (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    company_name character varying(255) NOT NULL,
    company_logo_url character varying(512),
    industry character varying(100),
    company_size character varying(50),
    website character varying(255),
    description text,
    location character varying(255),
    is_verified boolean DEFAULT false NOT NULL,
    verified_at timestamp without time zone,
    verified_by uuid,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.employer_profiles OWNER TO abilitybridge;

--
-- TOC entry 247 (class 1259 OID 17248)
-- Name: employer_ratings; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.employer_ratings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    employer_id uuid NOT NULL,
    rater_id uuid NOT NULL,
    score integer NOT NULL,
    comment text,
    is_anonymous boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT employer_ratings_score_check CHECK (((score >= 1) AND (score <= 5)))
);


ALTER TABLE public.employer_ratings OWNER TO abilitybridge;

--
-- TOC entry 221 (class 1259 OID 16390)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO abilitybridge;

--
-- TOC entry 246 (class 1259 OID 17224)
-- Name: inclusivity_badges; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.inclusivity_badges (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    employer_id uuid NOT NULL,
    tier character varying(20) NOT NULL,
    awarded_at timestamp without time zone DEFAULT now() NOT NULL,
    awarded_by uuid NOT NULL,
    valid_until timestamp without time zone,
    is_active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.inclusivity_badges OWNER TO abilitybridge;

--
-- TOC entry 253 (class 1259 OID 17428)
-- Name: interview_schedules; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.interview_schedules (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    job_id uuid NOT NULL,
    applicant_id uuid NOT NULL,
    employer_id uuid NOT NULL,
    scheduled_at timestamp without time zone NOT NULL,
    duration_mins integer DEFAULT 60 NOT NULL,
    format character varying(30),
    meeting_url character varying(512),
    notes text,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.interview_schedules OWNER TO abilitybridge;

--
-- TOC entry 239 (class 1259 OID 17024)
-- Name: job_applications; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.job_applications (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    job_id uuid NOT NULL,
    applicant_id uuid NOT NULL,
    apply_format character varying(30) NOT NULL,
    submission_url character varying(512),
    cover_letter text,
    status character varying(30) NOT NULL,
    anonymous_mode boolean DEFAULT false NOT NULL,
    match_score numeric(5,2),
    applied_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.job_applications OWNER TO abilitybridge;

--
-- TOC entry 237 (class 1259 OID 16977)
-- Name: job_listings; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.job_listings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    employer_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description text NOT NULL,
    job_type character varying(30) NOT NULL,
    location character varying(255),
    is_remote boolean DEFAULT false NOT NULL,
    salary_min numeric(12,2),
    salary_max numeric(12,2),
    currency character varying(3) DEFAULT 'USD'::character varying NOT NULL,
    status character varying(30) NOT NULL,
    application_deadline timestamp without time zone,
    accommodations_offered text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.job_listings OWNER TO abilitybridge;

--
-- TOC entry 240 (class 1259 OID 17057)
-- Name: job_match_scores; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.job_match_scores (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    job_id uuid NOT NULL,
    score numeric(5,2) NOT NULL,
    computed_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.job_match_scores OWNER TO abilitybridge;

--
-- TOC entry 238 (class 1259 OID 17005)
-- Name: job_required_skills; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.job_required_skills (
    job_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    is_required boolean DEFAULT true NOT NULL
);


ALTER TABLE public.job_required_skills OWNER TO abilitybridge;

--
-- TOC entry 248 (class 1259 OID 17287)
-- Name: mentor_profiles; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.mentor_profiles (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    industry character varying(100),
    disability_type character varying(255),
    career_stage character varying(50),
    bio text,
    offers_cv_review boolean DEFAULT false NOT NULL,
    offers_interview boolean DEFAULT false NOT NULL,
    offers_career_adv boolean DEFAULT false NOT NULL,
    max_mentees integer DEFAULT 3 NOT NULL,
    is_available boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.mentor_profiles OWNER TO abilitybridge;

--
-- TOC entry 249 (class 1259 OID 17318)
-- Name: mentorship_requests; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.mentorship_requests (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    mentor_id uuid NOT NULL,
    mentee_id uuid NOT NULL,
    message text,
    status character varying(30) NOT NULL,
    requested_at timestamp without time zone DEFAULT now() NOT NULL,
    responded_at timestamp without time zone
);


ALTER TABLE public.mentorship_requests OWNER TO abilitybridge;

--
-- TOC entry 252 (class 1259 OID 17401)
-- Name: messages; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.messages (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    conversation_id uuid NOT NULL,
    sender_id uuid NOT NULL,
    content text,
    message_format character varying(10) DEFAULT 'TEXT'::character varying NOT NULL,
    media_url character varying(512),
    is_read boolean DEFAULT false NOT NULL,
    sent_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.messages OWNER TO abilitybridge;

--
-- TOC entry 233 (class 1259 OID 16819)
-- Name: micro_tasks; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.micro_tasks (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    poster_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description text NOT NULL,
    category character varying(100),
    pay_rate numeric(10,2) NOT NULL,
    currency character varying(3) DEFAULT 'USD'::character varying NOT NULL,
    requirements text,
    deadline timestamp without time zone,
    status character varying(30) NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.micro_tasks OWNER TO abilitybridge;

--
-- TOC entry 254 (class 1259 OID 17460)
-- Name: notifications; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.notifications (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    type character varying(50) NOT NULL,
    title character varying(255) NOT NULL,
    body text,
    reference_id uuid,
    is_read boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.notifications OWNER TO abilitybridge;

--
-- TOC entry 232 (class 1259 OID 16773)
-- Name: portfolio_items; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.portfolio_items (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    item_type character varying(30) NOT NULL,
    url character varying(512),
    reference_id uuid,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.portfolio_items OWNER TO abilitybridge;

--
-- TOC entry 224 (class 1259 OID 16587)
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.refresh_tokens (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    token character varying(512) NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    revoked boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.refresh_tokens OWNER TO abilitybridge;

--
-- TOC entry 226 (class 1259 OID 16634)
-- Name: seeker_disability_info; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.seeker_disability_info (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    disability_type character varying(255),
    disclosure_level character varying(20) DEFAULT 'PRIVATE'::character varying NOT NULL,
    support_needs text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.seeker_disability_info OWNER TO abilitybridge;

--
-- TOC entry 225 (class 1259 OID 16610)
-- Name: seeker_profiles; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.seeker_profiles (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    full_name character varying(255) NOT NULL,
    headline character varying(255),
    bio text,
    location character varying(255),
    profile_picture_url character varying(512),
    cv_url character varying(512),
    video_profile_url character varying(512),
    voice_profile_url character varying(512),
    anonymous_mode boolean DEFAULT false NOT NULL,
    target_job_category character varying(100),
    availability character varying(50),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.seeker_profiles OWNER TO abilitybridge;

--
-- TOC entry 229 (class 1259 OID 16698)
-- Name: seeker_skills; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.seeker_skills (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    proficiency character varying(20) DEFAULT 'BEGINNER'::character varying NOT NULL,
    verified boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.seeker_skills OWNER TO abilitybridge;

--
-- TOC entry 231 (class 1259 OID 16745)
-- Name: skill_badges; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.skill_badges (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    course_id uuid,
    awarded_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.skill_badges OWNER TO abilitybridge;

--
-- TOC entry 228 (class 1259 OID 16686)
-- Name: skills; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.skills (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(100) NOT NULL,
    category character varying(100),
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.skills OWNER TO abilitybridge;

--
-- TOC entry 234 (class 1259 OID 16845)
-- Name: task_applications; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.task_applications (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    task_id uuid NOT NULL,
    applicant_id uuid NOT NULL,
    cover_note text,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    applied_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.task_applications OWNER TO abilitybridge;

--
-- TOC entry 235 (class 1259 OID 16872)
-- Name: task_completions; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.task_completions (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    task_id uuid NOT NULL,
    worker_id uuid NOT NULL,
    submission_url character varying(512),
    submission_notes text,
    submitted_at timestamp without time zone DEFAULT now() NOT NULL,
    approved_at timestamp without time zone,
    payment_status character varying(30) NOT NULL,
    payment_reference character varying(255)
);


ALTER TABLE public.task_completions OWNER TO abilitybridge;

--
-- TOC entry 236 (class 1259 OID 16897)
-- Name: task_ratings; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.task_ratings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    task_id uuid NOT NULL,
    rater_id uuid NOT NULL,
    ratee_id uuid NOT NULL,
    score integer NOT NULL,
    comment text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT task_ratings_score_check CHECK (((score >= 1) AND (score <= 5)))
);


ALTER TABLE public.task_ratings OWNER TO abilitybridge;

--
-- TOC entry 222 (class 1259 OID 16529)
-- Name: users; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.users (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    email character varying(255),
    phone character varying(30),
    password_hash character varying(255),
    role character varying(50) NOT NULL,
    provider character varying(50) NOT NULL,
    provider_id character varying(255),
    status character varying(50) NOT NULL,
    email_verified boolean DEFAULT false NOT NULL,
    phone_verified boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    last_login_at timestamp without time zone,
    CONSTRAINT chk_contact CHECK (((email IS NOT NULL) OR (phone IS NOT NULL)))
);


ALTER TABLE public.users OWNER TO abilitybridge;

--
-- TOC entry 245 (class 1259 OID 17195)
-- Name: workplace_reality_scores; Type: TABLE; Schema: public; Owner: abilitybridge
--

CREATE TABLE public.workplace_reality_scores (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    employer_id uuid NOT NULL,
    overall_score numeric(4,2) DEFAULT 0 NOT NULL,
    hiring_score numeric(4,2) DEFAULT 0 NOT NULL,
    accommodation_score numeric(4,2) DEFAULT 0 NOT NULL,
    community_score numeric(4,2) DEFAULT 0 NOT NULL,
    total_hires integer DEFAULT 0 NOT NULL,
    accommodation_fulfil_rate numeric(5,2) DEFAULT 0 NOT NULL,
    computed_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.workplace_reality_scores OWNER TO abilitybridge;

--
-- TOC entry 5456 (class 0 OID 16556)
-- Dependencies: 223
-- Data for Name: accessibility_settings; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.accessibility_settings (id, user_id, screen_reader, high_contrast, voice_nav, font_size, dyslexia_font, sign_lang_video, created_at, updated_at) FROM stdin;
aba6be32-eea5-4378-ab8b-b372894437d3	bd89089d-4120-429c-9b4b-6500d44d593f	f	f	f	MEDIUM	f	f	2026-04-22 12:15:24.4983	2026-04-22 12:15:40.213395
5d553bcc-5d26-4849-bca8-71e6b6165a99	ecbfc1ca-998b-40d4-9e5a-abdd42c7632c	f	f	f	MEDIUM	f	f	2026-04-28 10:26:08.663864	2026-04-28 10:26:14.886504
da5438a3-ad53-4c41-bd11-c1cb3851c72c	e40c6a68-820c-4d2c-ac60-8039a5dee663	f	f	f	MEDIUM	f	f	2026-04-29 12:38:07.336039	2026-04-29 12:42:05.561542
\.


--
-- TOC entry 5477 (class 0 OID 17165)
-- Dependencies: 244
-- Data for Name: accommodation_agreements; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.accommodation_agreements (id, negotiation_id, agreed_terms, agreed_at, seeker_signed, employer_signed) FROM stdin;
\.


--
-- TOC entry 5476 (class 0 OID 17141)
-- Dependencies: 243
-- Data for Name: accommodation_messages; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.accommodation_messages (id, negotiation_id, sender_id, content, sent_at) FROM stdin;
\.


--
-- TOC entry 5474 (class 0 OID 17093)
-- Dependencies: 241
-- Data for Name: accommodation_needs; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.accommodation_needs (id, user_id, need_type, description, is_mandatory, created_at) FROM stdin;
\.


--
-- TOC entry 5475 (class 0 OID 17113)
-- Dependencies: 242
-- Data for Name: accommodation_negotiations; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.accommodation_negotiations (id, job_id, seeker_id, employer_id, status, compatibility, opened_at, resolved_at) FROM stdin;
\.


--
-- TOC entry 5484 (class 0 OID 17382)
-- Dependencies: 251
-- Data for Name: conversation_participants; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.conversation_participants (conversation_id, user_id, joined_at) FROM stdin;
\.


--
-- TOC entry 5483 (class 0 OID 17371)
-- Dependencies: 250
-- Data for Name: conversations; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.conversations (id, reference_id, created_at, type) FROM stdin;
\.


--
-- TOC entry 5463 (class 0 OID 16725)
-- Dependencies: 230
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.courses (id, title, provider, url, skill_id, is_free, duration_hours, created_at) FROM stdin;
f4c710cf-2330-49d9-a6d6-497d3a6c80e9	Python for Everybody	Coursera	https://coursera.org/specializations/python	\N	t	30	2026-04-14 19:53:09.16577
3850070c-bb11-4409-9dfe-5e6759529cbc	SQL for Data Science	Coursera	https://coursera.org/learn/sql-for-data-science	\N	t	12	2026-04-14 19:53:09.16577
a2066c44-255d-4292-a7cc-0472ee7d3b3b	AWS Cloud Practitioner	AWS	https://aws.amazon.com/training/digital	\N	t	15	2026-04-14 19:53:09.16577
abfba3bc-b33c-42ad-abda-fdb9dcda2c9a	UX Design Fundamentals	Google	https://grow.google/certificates/ux-design	\N	t	40	2026-04-14 19:53:09.16577
eae8f390-e242-472b-b61a-6c5ca30c929d	Project Management Basics	Google	https://grow.google/certificates/project-management	\N	t	40	2026-04-14 19:53:09.16577
\.


--
-- TOC entry 5488 (class 0 OID 17491)
-- Dependencies: 255
-- Data for Name: discrimination_reports; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.discrimination_reports (id, reporter_id, employer_id, description, evidence_url, is_anonymous, status, submitted_at, reviewed_at, reviewed_by) FROM stdin;
\.


--
-- TOC entry 5489 (class 0 OID 17524)
-- Dependencies: 256
-- Data for Name: employer_flags; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.employer_flags (id, employer_id, flagged_at, flagged_by, reason, is_resolved, resolved_at, admin_notes) FROM stdin;
\.


--
-- TOC entry 5460 (class 0 OID 16657)
-- Dependencies: 227
-- Data for Name: employer_profiles; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.employer_profiles (id, user_id, company_name, company_logo_url, industry, company_size, website, description, location, is_verified, verified_at, verified_by, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5480 (class 0 OID 17248)
-- Dependencies: 247
-- Data for Name: employer_ratings; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.employer_ratings (id, employer_id, rater_id, score, comment, is_anonymous, created_at) FROM stdin;
\.


--
-- TOC entry 5454 (class 0 OID 16390)
-- Dependencies: 221
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	initial schema	SQL	V1__initial_schema.sql	528884308	abilitybridge	2026-04-14 19:53:08.824529	270	t
2	2	seed data	SQL	V2__seed_data.sql	-822605610	abilitybridge	2026-04-14 19:53:09.15538	31	t
3	3	fix score columns to integer	SQL	V3__fix_score_columns_to_integer.sql	-2042877012	abilitybridge	2026-04-15 09:04:49.809468	49	t
\.


--
-- TOC entry 5479 (class 0 OID 17224)
-- Dependencies: 246
-- Data for Name: inclusivity_badges; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.inclusivity_badges (id, employer_id, tier, awarded_at, awarded_by, valid_until, is_active) FROM stdin;
\.


--
-- TOC entry 5486 (class 0 OID 17428)
-- Dependencies: 253
-- Data for Name: interview_schedules; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.interview_schedules (id, job_id, applicant_id, employer_id, scheduled_at, duration_mins, format, meeting_url, notes, created_at) FROM stdin;
\.


--
-- TOC entry 5472 (class 0 OID 17024)
-- Dependencies: 239
-- Data for Name: job_applications; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.job_applications (id, job_id, applicant_id, apply_format, submission_url, cover_letter, status, anonymous_mode, match_score, applied_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5470 (class 0 OID 16977)
-- Dependencies: 237
-- Data for Name: job_listings; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.job_listings (id, employer_id, title, description, job_type, location, is_remote, salary_min, salary_max, currency, status, application_deadline, accommodations_offered, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5473 (class 0 OID 17057)
-- Dependencies: 240
-- Data for Name: job_match_scores; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.job_match_scores (id, user_id, job_id, score, computed_at) FROM stdin;
\.


--
-- TOC entry 5471 (class 0 OID 17005)
-- Dependencies: 238
-- Data for Name: job_required_skills; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.job_required_skills (job_id, skill_id, is_required) FROM stdin;
\.


--
-- TOC entry 5481 (class 0 OID 17287)
-- Dependencies: 248
-- Data for Name: mentor_profiles; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.mentor_profiles (id, user_id, industry, disability_type, career_stage, bio, offers_cv_review, offers_interview, offers_career_adv, max_mentees, is_available, created_at, updated_at) FROM stdin;
1d242d07-acea-4834-941d-38acac5bce71	42d1c10c-b374-4f22-8caf-f82ed6ec6aeb	Healthcare	\N	\N		f	f	f	3	t	2026-04-22 12:23:18.904746	2026-04-22 12:23:18.916765
\.


--
-- TOC entry 5482 (class 0 OID 17318)
-- Dependencies: 249
-- Data for Name: mentorship_requests; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.mentorship_requests (id, mentor_id, mentee_id, message, status, requested_at, responded_at) FROM stdin;
\.


--
-- TOC entry 5485 (class 0 OID 17401)
-- Dependencies: 252
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.messages (id, conversation_id, sender_id, content, message_format, media_url, is_read, sent_at) FROM stdin;
\.


--
-- TOC entry 5466 (class 0 OID 16819)
-- Dependencies: 233
-- Data for Name: micro_tasks; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.micro_tasks (id, poster_id, title, description, category, pay_rate, currency, requirements, deadline, status, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5487 (class 0 OID 17460)
-- Dependencies: 254
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.notifications (id, user_id, type, title, body, reference_id, is_read, created_at) FROM stdin;
\.


--
-- TOC entry 5465 (class 0 OID 16773)
-- Dependencies: 232
-- Data for Name: portfolio_items; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.portfolio_items (id, user_id, title, description, item_type, url, reference_id, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5457 (class 0 OID 16587)
-- Dependencies: 224
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.refresh_tokens (id, user_id, token, expires_at, revoked, created_at) FROM stdin;
670ff3f7-1df4-4ce2-b7ca-bb5d5dad2848	bd89089d-4120-429c-9b4b-6500d44d593f	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0aW5AZ21haWwuY29tIiwiaWF0IjoxNzc2ODU2NDAyLCJleHAiOjE3Nzc0NjEyMDJ9.lig9-VFn_jm5j3sahPJmvPjpjGAuo8hPgUPn8x-wfIocqvkTw7MKrbaEBrxrB4Oq	2026-04-29 12:13:22.700328	t	2026-04-22 12:13:22.700328
11d15337-fb71-48c5-8f5a-f3526233aad7	bd89089d-4120-429c-9b4b-6500d44d593f	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0aW5AZ21haWwuY29tIiwiaWF0IjoxNzc2ODU2NzM4LCJleHAiOjE3Nzc0NjE1Mzh9.AupP-Ke3jwf4Ftt9hQ4tQpkUpybJXIzXCFJKDgPIjgPTBCQBuKfT2b3B5gN_gLYg	2026-04-29 12:18:58.255927	t	2026-04-22 12:18:58.255927
226028d1-d262-4c4a-a973-44f5c3223152	42d1c10c-b374-4f22-8caf-f82ed6ec6aeb	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0b21AZ21haWwuY29tIiwiaWF0IjoxNzc2ODU2OTE1LCJleHAiOjE3Nzc0NjE3MTV9.DYVVgjfLDsCuMPjW9p8OtNFVuqRC6__MbYQ6kx-Vpvf9WNrmavM5px8NkYYqbUIv	2026-04-29 12:21:55.631157	t	2026-04-22 12:21:55.631157
0045cfe1-f8b8-4324-9d06-1e84f5471a21	b84c4c3c-2d10-446d-8aaf-ca6df2779cef	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhekBnbWFpbC5jb20iLCJpYXQiOjE3NzY4NTcxMjksImV4cCI6MTc3NzQ2MTkyOX0.Xta1GypKhebTYUlVZQZIL-hEmFaY25ZHsXSg_fXhwVtDJhcnkADR7rBjL8VTUKL-	2026-04-29 12:25:29.560768	f	2026-04-22 12:25:29.560768
26665a85-078c-493c-b382-8ee44ac7c0af	c2442ddd-0e39-40d2-b7c2-a6c60b0140f7	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJtYmFuZ3VldHNodW5nYm92ZUBnbWFpbC5jb20iLCJpYXQiOjE3NzY4NTgxMjIsImV4cCI6MTc3NzQ2MjkyMn0.A6YRC1j_ATDsQMbKEy7XUDW1KPCSnOVTDfFnb5e0tuRFUxYG83jjUTxfiZtTgOGn	2026-04-29 12:42:02.880647	f	2026-04-22 12:42:02.880647
cadfaa34-8492-4db4-823a-6a19fb603cc0	ecbfc1ca-998b-40d4-9e5a-abdd42c7632c	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJnZmZAZ21haWwuY29tIiwiaWF0IjoxNzc3MzY4MzI1LCJleHAiOjE3Nzc5NzMxMjV9.NLuwwhDpv8W0mZGz33OFd0C6QY9Ui2iIatGFrKf6tSXHzileDdA1QG5c2HqV5IFm	2026-05-05 10:25:25.183171	f	2026-04-28 10:25:25.183171
9cd91f50-31a5-48ae-9325-3775bc28c277	e40c6a68-820c-4d2c-ac60-8039a5dee663	eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJTQGdtYWlsLmNvbSIsImlhdCI6MTc3NzQ2MjY1MSwiZXhwIjoxNzc4MDY3NDUxfQ.X5t3i1-dgjovyDs_yNFuIUdvtw4yHOvKl_e7jfGPByIPH6B-xkeLUKvncmedi7P_	2026-05-06 12:37:31.954876	f	2026-04-29 12:37:31.954876
\.


--
-- TOC entry 5459 (class 0 OID 16634)
-- Dependencies: 226
-- Data for Name: seeker_disability_info; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.seeker_disability_info (id, user_id, disability_type, disclosure_level, support_needs, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5458 (class 0 OID 16610)
-- Dependencies: 225
-- Data for Name: seeker_profiles; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.seeker_profiles (id, user_id, full_name, headline, bio, location, profile_picture_url, cv_url, video_profile_url, voice_profile_url, anonymous_mode, target_job_category, availability, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5462 (class 0 OID 16698)
-- Dependencies: 229
-- Data for Name: seeker_skills; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.seeker_skills (id, user_id, skill_id, proficiency, verified, created_at) FROM stdin;
\.


--
-- TOC entry 5464 (class 0 OID 16745)
-- Dependencies: 231
-- Data for Name: skill_badges; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.skill_badges (id, user_id, skill_id, course_id, awarded_at) FROM stdin;
\.


--
-- TOC entry 5461 (class 0 OID 16686)
-- Dependencies: 228
-- Data for Name: skills; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.skills (id, name, category, created_at) FROM stdin;
e667bbcc-9d86-4fd6-8ad1-ee1798f464ec	JavaScript	Programming	2026-04-14 19:53:09.16577
d59c0720-043f-469d-a23a-82f6cba52a81	Python	Programming	2026-04-14 19:53:09.16577
f52d013a-281e-4df4-9063-19ee0afa790a	Java	Programming	2026-04-14 19:53:09.16577
7c472f14-4921-479d-ae53-7c1c41b957a3	SQL	Data	2026-04-14 19:53:09.16577
f3434cfd-8f0e-41a5-a72b-884f7ec83ffa	Data Analysis	Data	2026-04-14 19:53:09.16577
d3b9c3a8-48a6-44f8-b9e7-7323b81b704d	Machine Learning	Data	2026-04-14 19:53:09.16577
0b4cbbe0-ea04-4081-b2fd-af4e52178696	Project Management	Management	2026-04-14 19:53:09.16577
75e477ab-bf9e-404f-a12f-9b986fabdcd9	Communication	Soft Skills	2026-04-14 19:53:09.16577
87ed1c13-92a7-4ca0-b147-dea2d5376e57	Customer Service	Soft Skills	2026-04-14 19:53:09.16577
2ae036c8-bb12-4210-8f00-a31149cf254d	Graphic Design	Design	2026-04-14 19:53:09.16577
727ca19c-4dab-41b2-91db-ae0e5ca8bacb	UX Design	Design	2026-04-14 19:53:09.16577
95903c49-1d21-483e-b8b2-2161181c8766	Content Writing	Marketing	2026-04-14 19:53:09.16577
f9ce0d6a-0cd0-460f-9f96-10c9bc019980	Social Media	Marketing	2026-04-14 19:53:09.16577
7d10316b-7247-4120-be76-fb565380b9a2	Accounting	Finance	2026-04-14 19:53:09.16577
90e5a416-f34e-41cf-bc22-36ef5011aef5	Microsoft Excel	Tools	2026-04-14 19:53:09.16577
57591862-0ec8-4d79-af6d-422258fce9f0	React	Programming	2026-04-14 19:53:09.16577
645be144-6eb3-4aec-89a3-2ab770c9ad69	Spring Boot	Programming	2026-04-14 19:53:09.16577
29ee4964-5568-4a13-8ac2-737bc5318ede	Docker	DevOps	2026-04-14 19:53:09.16577
df967e99-49f8-48c8-9471-4d8bced4b6e0	AWS	Cloud	2026-04-14 19:53:09.16577
5cd2e3bd-8c66-4292-bede-f593b2515115	Technical Writing	Soft Skills	2026-04-14 19:53:09.16577
\.


--
-- TOC entry 5467 (class 0 OID 16845)
-- Dependencies: 234
-- Data for Name: task_applications; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.task_applications (id, task_id, applicant_id, cover_note, status, applied_at) FROM stdin;
\.


--
-- TOC entry 5468 (class 0 OID 16872)
-- Dependencies: 235
-- Data for Name: task_completions; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.task_completions (id, task_id, worker_id, submission_url, submission_notes, submitted_at, approved_at, payment_status, payment_reference) FROM stdin;
\.


--
-- TOC entry 5469 (class 0 OID 16897)
-- Dependencies: 236
-- Data for Name: task_ratings; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.task_ratings (id, task_id, rater_id, ratee_id, score, comment, created_at) FROM stdin;
\.


--
-- TOC entry 5455 (class 0 OID 16529)
-- Dependencies: 222
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.users (id, email, phone, password_hash, role, provider, provider_id, status, email_verified, phone_verified, created_at, updated_at, last_login_at) FROM stdin;
07173d79-15ab-484f-87d3-73cd2de164b3	admin@abilitybridge.io	\N	$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewmT9xBrRhCkzJwG	ADMIN	LOCAL	\N	ACTIVE	t	f	2026-04-14 19:53:09.16577	2026-04-14 19:53:09.16577	\N
bd89089d-4120-429c-9b4b-6500d44d593f	tin@gmail.com	\N	$2a$12$zKkLCwejtyiEU8PN1xUD0.Ait8dEHZomb/AtDAnIrx9x4QwWP8JAi	EMPLOYER	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-22 12:13:22.393371	2026-04-22 12:18:58.260928	2026-04-22 12:18:58.250943
42d1c10c-b374-4f22-8caf-f82ed6ec6aeb	tom@gmail.com	\N	$2a$12$XIcs2Hqvki2MWlKH/j4ReOg6Ekd51wgmhPl3RSZzzLaKNwMfNIwZW	MENTOR	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-22 12:21:55.626	2026-04-22 12:21:55.626	\N
b84c4c3c-2d10-446d-8aaf-ca6df2779cef	az@gmail.com	\N	$2a$12$.YjoQA7y/rIl6tyZ2mPxIeYMUJmpxiHsY/gS/pGXxQeVfRxC/YI6K	SEEKER	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-22 12:25:29.555759	2026-04-22 12:25:29.555759	\N
c2442ddd-0e39-40d2-b7c2-a6c60b0140f7	mbanguetshungbove@gmail.com	\N	$2a$12$ayZHuAwXbtPHDynWoJnNMeAaeZnUuvuT353hIfSfzn5/pHRQCPqr6	SEEKER	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-22 12:42:02.874544	2026-04-22 12:42:02.874544	\N
ecbfc1ca-998b-40d4-9e5a-abdd42c7632c	gff@gmail.com	\N	$2a$12$jZT2RxStV8ckKvPZdOHOfunmIzchCDrmwjqtbt8c2HqYmSUPVN7ne	SEEKER	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-28 10:25:24.515015	2026-04-28 10:25:24.515015	\N
e40c6a68-820c-4d2c-ac60-8039a5dee663	S@gmail.com	\N	$2a$12$fv4eJlAyO7pzWLioPClBWuS2/XTgw2FLiQhNYDsOK6K7uEvy7gdpa	SEEKER	LOCAL	\N	PENDING_VERIFICATION	f	f	2026-04-29 12:37:31.579761	2026-04-29 12:37:31.579761	\N
\.


--
-- TOC entry 5478 (class 0 OID 17195)
-- Dependencies: 245
-- Data for Name: workplace_reality_scores; Type: TABLE DATA; Schema: public; Owner: abilitybridge
--

COPY public.workplace_reality_scores (id, employer_id, overall_score, hiring_score, accommodation_score, community_score, total_hires, accommodation_fulfil_rate, computed_at) FROM stdin;
\.


--
-- TOC entry 5144 (class 2606 OID 16579)
-- Name: accessibility_settings accessibility_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accessibility_settings
    ADD CONSTRAINT accessibility_settings_pkey PRIMARY KEY (id);


--
-- TOC entry 5146 (class 2606 OID 16581)
-- Name: accessibility_settings accessibility_settings_user_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accessibility_settings
    ADD CONSTRAINT accessibility_settings_user_id_key UNIQUE (user_id);


--
-- TOC entry 5217 (class 2606 OID 17181)
-- Name: accommodation_agreements accommodation_agreements_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_agreements
    ADD CONSTRAINT accommodation_agreements_pkey PRIMARY KEY (id);


--
-- TOC entry 5215 (class 2606 OID 17154)
-- Name: accommodation_messages accommodation_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_messages
    ADD CONSTRAINT accommodation_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 5211 (class 2606 OID 17107)
-- Name: accommodation_needs accommodation_needs_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_needs
    ADD CONSTRAINT accommodation_needs_pkey PRIMARY KEY (id);


--
-- TOC entry 5213 (class 2606 OID 17125)
-- Name: accommodation_negotiations accommodation_negotiations_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_negotiations
    ADD CONSTRAINT accommodation_negotiations_pkey PRIMARY KEY (id);


--
-- TOC entry 5237 (class 2606 OID 17390)
-- Name: conversation_participants conversation_participants_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.conversation_participants
    ADD CONSTRAINT conversation_participants_pkey PRIMARY KEY (conversation_id, user_id);


--
-- TOC entry 5235 (class 2606 OID 17381)
-- Name: conversations conversations_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.conversations
    ADD CONSTRAINT conversations_pkey PRIMARY KEY (id);


--
-- TOC entry 5173 (class 2606 OID 16739)
-- Name: courses courses_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (id);


--
-- TOC entry 5247 (class 2606 OID 17508)
-- Name: discrimination_reports discrimination_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.discrimination_reports
    ADD CONSTRAINT discrimination_reports_pkey PRIMARY KEY (id);


--
-- TOC entry 5250 (class 2606 OID 17538)
-- Name: employer_flags employer_flags_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_flags
    ADD CONSTRAINT employer_flags_pkey PRIMARY KEY (id);


--
-- TOC entry 5160 (class 2606 OID 16673)
-- Name: employer_profiles employer_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_profiles
    ADD CONSTRAINT employer_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 5162 (class 2606 OID 16675)
-- Name: employer_profiles employer_profiles_user_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_profiles
    ADD CONSTRAINT employer_profiles_user_id_key UNIQUE (user_id);


--
-- TOC entry 5225 (class 2606 OID 17264)
-- Name: employer_ratings employer_ratings_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_ratings
    ADD CONSTRAINT employer_ratings_pkey PRIMARY KEY (id);


--
-- TOC entry 5132 (class 2606 OID 16405)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 5223 (class 2606 OID 17237)
-- Name: inclusivity_badges inclusivity_badges_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.inclusivity_badges
    ADD CONSTRAINT inclusivity_badges_pkey PRIMARY KEY (id);


--
-- TOC entry 5242 (class 2606 OID 17444)
-- Name: interview_schedules interview_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.interview_schedules
    ADD CONSTRAINT interview_schedules_pkey PRIMARY KEY (id);


--
-- TOC entry 5202 (class 2606 OID 17046)
-- Name: job_applications job_applications_job_id_applicant_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_applications
    ADD CONSTRAINT job_applications_job_id_applicant_id_key UNIQUE (job_id, applicant_id);


--
-- TOC entry 5204 (class 2606 OID 17044)
-- Name: job_applications job_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_applications
    ADD CONSTRAINT job_applications_pkey PRIMARY KEY (id);


--
-- TOC entry 5197 (class 2606 OID 16999)
-- Name: job_listings job_listings_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_listings
    ADD CONSTRAINT job_listings_pkey PRIMARY KEY (id);


--
-- TOC entry 5207 (class 2606 OID 17068)
-- Name: job_match_scores job_match_scores_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_match_scores
    ADD CONSTRAINT job_match_scores_pkey PRIMARY KEY (id);


--
-- TOC entry 5209 (class 2606 OID 17070)
-- Name: job_match_scores job_match_scores_user_id_job_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_match_scores
    ADD CONSTRAINT job_match_scores_user_id_job_id_key UNIQUE (user_id, job_id);


--
-- TOC entry 5200 (class 2606 OID 17013)
-- Name: job_required_skills job_required_skills_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_required_skills
    ADD CONSTRAINT job_required_skills_pkey PRIMARY KEY (job_id, skill_id);


--
-- TOC entry 5227 (class 2606 OID 17310)
-- Name: mentor_profiles mentor_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentor_profiles
    ADD CONSTRAINT mentor_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 5229 (class 2606 OID 17312)
-- Name: mentor_profiles mentor_profiles_user_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentor_profiles
    ADD CONSTRAINT mentor_profiles_user_id_key UNIQUE (user_id);


--
-- TOC entry 5231 (class 2606 OID 17334)
-- Name: mentorship_requests mentorship_requests_mentor_id_mentee_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentorship_requests
    ADD CONSTRAINT mentorship_requests_mentor_id_mentee_id_key UNIQUE (mentor_id, mentee_id);


--
-- TOC entry 5233 (class 2606 OID 17332)
-- Name: mentorship_requests mentorship_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentorship_requests
    ADD CONSTRAINT mentorship_requests_pkey PRIMARY KEY (id);


--
-- TOC entry 5240 (class 2606 OID 17417)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 5182 (class 2606 OID 16839)
-- Name: micro_tasks micro_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.micro_tasks
    ADD CONSTRAINT micro_tasks_pkey PRIMARY KEY (id);


--
-- TOC entry 5245 (class 2606 OID 17475)
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 5179 (class 2606 OID 16788)
-- Name: portfolio_items portfolio_items_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.portfolio_items
    ADD CONSTRAINT portfolio_items_pkey PRIMARY KEY (id);


--
-- TOC entry 5148 (class 2606 OID 16602)
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 5150 (class 2606 OID 16604)
-- Name: refresh_tokens refresh_tokens_token_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_key UNIQUE (token);


--
-- TOC entry 5156 (class 2606 OID 16649)
-- Name: seeker_disability_info seeker_disability_info_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_disability_info
    ADD CONSTRAINT seeker_disability_info_pkey PRIMARY KEY (id);


--
-- TOC entry 5158 (class 2606 OID 16651)
-- Name: seeker_disability_info seeker_disability_info_user_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_disability_info
    ADD CONSTRAINT seeker_disability_info_user_id_key UNIQUE (user_id);


--
-- TOC entry 5152 (class 2606 OID 16626)
-- Name: seeker_profiles seeker_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_profiles
    ADD CONSTRAINT seeker_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 5154 (class 2606 OID 16628)
-- Name: seeker_profiles seeker_profiles_user_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_profiles
    ADD CONSTRAINT seeker_profiles_user_id_key UNIQUE (user_id);


--
-- TOC entry 5169 (class 2606 OID 16712)
-- Name: seeker_skills seeker_skills_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_skills
    ADD CONSTRAINT seeker_skills_pkey PRIMARY KEY (id);


--
-- TOC entry 5171 (class 2606 OID 16714)
-- Name: seeker_skills seeker_skills_user_id_skill_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_skills
    ADD CONSTRAINT seeker_skills_user_id_skill_id_key UNIQUE (user_id, skill_id);


--
-- TOC entry 5175 (class 2606 OID 16755)
-- Name: skill_badges skill_badges_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skill_badges
    ADD CONSTRAINT skill_badges_pkey PRIMARY KEY (id);


--
-- TOC entry 5177 (class 2606 OID 16757)
-- Name: skill_badges skill_badges_user_id_skill_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skill_badges
    ADD CONSTRAINT skill_badges_user_id_skill_id_key UNIQUE (user_id, skill_id);


--
-- TOC entry 5164 (class 2606 OID 16697)
-- Name: skills skills_name_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skills
    ADD CONSTRAINT skills_name_key UNIQUE (name);


--
-- TOC entry 5166 (class 2606 OID 16695)
-- Name: skills skills_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skills
    ADD CONSTRAINT skills_pkey PRIMARY KEY (id);


--
-- TOC entry 5184 (class 2606 OID 16859)
-- Name: task_applications task_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_applications
    ADD CONSTRAINT task_applications_pkey PRIMARY KEY (id);


--
-- TOC entry 5186 (class 2606 OID 16861)
-- Name: task_applications task_applications_task_id_applicant_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_applications
    ADD CONSTRAINT task_applications_task_id_applicant_id_key UNIQUE (task_id, applicant_id);


--
-- TOC entry 5188 (class 2606 OID 16886)
-- Name: task_completions task_completions_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_completions
    ADD CONSTRAINT task_completions_pkey PRIMARY KEY (id);


--
-- TOC entry 5190 (class 2606 OID 16912)
-- Name: task_ratings task_ratings_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_ratings
    ADD CONSTRAINT task_ratings_pkey PRIMARY KEY (id);


--
-- TOC entry 5192 (class 2606 OID 16914)
-- Name: task_ratings task_ratings_task_id_rater_id_ratee_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_ratings
    ADD CONSTRAINT task_ratings_task_id_rater_id_ratee_id_key UNIQUE (task_id, rater_id, ratee_id);


--
-- TOC entry 5138 (class 2606 OID 16553)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 5140 (class 2606 OID 16555)
-- Name: users users_phone_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_key UNIQUE (phone);


--
-- TOC entry 5142 (class 2606 OID 16551)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 5219 (class 2606 OID 17218)
-- Name: workplace_reality_scores workplace_reality_scores_employer_id_key; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.workplace_reality_scores
    ADD CONSTRAINT workplace_reality_scores_employer_id_key UNIQUE (employer_id);


--
-- TOC entry 5221 (class 2606 OID 17216)
-- Name: workplace_reality_scores workplace_reality_scores_pkey; Type: CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.workplace_reality_scores
    ADD CONSTRAINT workplace_reality_scores_pkey PRIMARY KEY (id);


--
-- TOC entry 5133 (class 1259 OID 16406)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 5193 (class 1259 OID 17553)
-- Name: idx_job_listings_remote; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_job_listings_remote ON public.job_listings USING btree (is_remote);


--
-- TOC entry 5194 (class 1259 OID 17827)
-- Name: idx_job_listings_status; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_job_listings_status ON public.job_listings USING btree (status);


--
-- TOC entry 5195 (class 1259 OID 17554)
-- Name: idx_job_listings_title; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_job_listings_title ON public.job_listings USING gin (to_tsvector('english'::regconfig, (((title)::text || ' '::text) || description)));


--
-- TOC entry 5198 (class 1259 OID 17561)
-- Name: idx_job_req_skills; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_job_req_skills ON public.job_required_skills USING btree (job_id);


--
-- TOC entry 5205 (class 1259 OID 17559)
-- Name: idx_match_scores; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_match_scores ON public.job_match_scores USING btree (user_id, score DESC);


--
-- TOC entry 5238 (class 1259 OID 17556)
-- Name: idx_messages_conv; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_messages_conv ON public.messages USING btree (conversation_id, sent_at DESC);


--
-- TOC entry 5180 (class 1259 OID 17811)
-- Name: idx_micro_tasks_status; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_micro_tasks_status ON public.micro_tasks USING btree (status);


--
-- TOC entry 5243 (class 1259 OID 17557)
-- Name: idx_notifications_user; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_notifications_user ON public.notifications USING btree (user_id, is_read, created_at DESC);


--
-- TOC entry 5248 (class 1259 OID 17853)
-- Name: idx_reports_employer; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_reports_employer ON public.discrimination_reports USING btree (employer_id, status);


--
-- TOC entry 5167 (class 1259 OID 17560)
-- Name: idx_seeker_skills; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_seeker_skills ON public.seeker_skills USING btree (user_id);


--
-- TOC entry 5134 (class 1259 OID 17549)
-- Name: idx_users_email; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_users_email ON public.users USING btree (email);


--
-- TOC entry 5135 (class 1259 OID 17795)
-- Name: idx_users_role; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_users_role ON public.users USING btree (role);


--
-- TOC entry 5136 (class 1259 OID 17796)
-- Name: idx_users_status; Type: INDEX; Schema: public; Owner: abilitybridge
--

CREATE INDEX idx_users_status ON public.users USING btree (status);


--
-- TOC entry 5251 (class 2606 OID 16582)
-- Name: accessibility_settings accessibility_settings_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accessibility_settings
    ADD CONSTRAINT accessibility_settings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5285 (class 2606 OID 17182)
-- Name: accommodation_agreements accommodation_agreements_negotiation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_agreements
    ADD CONSTRAINT accommodation_agreements_negotiation_id_fkey FOREIGN KEY (negotiation_id) REFERENCES public.accommodation_negotiations(id);


--
-- TOC entry 5283 (class 2606 OID 17155)
-- Name: accommodation_messages accommodation_messages_negotiation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_messages
    ADD CONSTRAINT accommodation_messages_negotiation_id_fkey FOREIGN KEY (negotiation_id) REFERENCES public.accommodation_negotiations(id) ON DELETE CASCADE;


--
-- TOC entry 5284 (class 2606 OID 17160)
-- Name: accommodation_messages accommodation_messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_messages
    ADD CONSTRAINT accommodation_messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id);


--
-- TOC entry 5279 (class 2606 OID 17108)
-- Name: accommodation_needs accommodation_needs_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_needs
    ADD CONSTRAINT accommodation_needs_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5280 (class 2606 OID 17136)
-- Name: accommodation_negotiations accommodation_negotiations_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_negotiations
    ADD CONSTRAINT accommodation_negotiations_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5281 (class 2606 OID 17126)
-- Name: accommodation_negotiations accommodation_negotiations_job_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_negotiations
    ADD CONSTRAINT accommodation_negotiations_job_id_fkey FOREIGN KEY (job_id) REFERENCES public.job_listings(id);


--
-- TOC entry 5282 (class 2606 OID 17131)
-- Name: accommodation_negotiations accommodation_negotiations_seeker_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.accommodation_negotiations
    ADD CONSTRAINT accommodation_negotiations_seeker_id_fkey FOREIGN KEY (seeker_id) REFERENCES public.users(id);


--
-- TOC entry 5294 (class 2606 OID 17391)
-- Name: conversation_participants conversation_participants_conversation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.conversation_participants
    ADD CONSTRAINT conversation_participants_conversation_id_fkey FOREIGN KEY (conversation_id) REFERENCES public.conversations(id) ON DELETE CASCADE;


--
-- TOC entry 5295 (class 2606 OID 17396)
-- Name: conversation_participants conversation_participants_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.conversation_participants
    ADD CONSTRAINT conversation_participants_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5259 (class 2606 OID 16740)
-- Name: courses courses_skill_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_skill_id_fkey FOREIGN KEY (skill_id) REFERENCES public.skills(id);


--
-- TOC entry 5302 (class 2606 OID 17514)
-- Name: discrimination_reports discrimination_reports_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.discrimination_reports
    ADD CONSTRAINT discrimination_reports_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5303 (class 2606 OID 17509)
-- Name: discrimination_reports discrimination_reports_reporter_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.discrimination_reports
    ADD CONSTRAINT discrimination_reports_reporter_id_fkey FOREIGN KEY (reporter_id) REFERENCES public.users(id);


--
-- TOC entry 5304 (class 2606 OID 17519)
-- Name: discrimination_reports discrimination_reports_reviewed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.discrimination_reports
    ADD CONSTRAINT discrimination_reports_reviewed_by_fkey FOREIGN KEY (reviewed_by) REFERENCES public.users(id);


--
-- TOC entry 5305 (class 2606 OID 17539)
-- Name: employer_flags employer_flags_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_flags
    ADD CONSTRAINT employer_flags_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5306 (class 2606 OID 17544)
-- Name: employer_flags employer_flags_flagged_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_flags
    ADD CONSTRAINT employer_flags_flagged_by_fkey FOREIGN KEY (flagged_by) REFERENCES public.users(id);


--
-- TOC entry 5255 (class 2606 OID 16676)
-- Name: employer_profiles employer_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_profiles
    ADD CONSTRAINT employer_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5256 (class 2606 OID 16681)
-- Name: employer_profiles employer_profiles_verified_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_profiles
    ADD CONSTRAINT employer_profiles_verified_by_fkey FOREIGN KEY (verified_by) REFERENCES public.users(id);


--
-- TOC entry 5289 (class 2606 OID 17265)
-- Name: employer_ratings employer_ratings_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_ratings
    ADD CONSTRAINT employer_ratings_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5290 (class 2606 OID 17270)
-- Name: employer_ratings employer_ratings_rater_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.employer_ratings
    ADD CONSTRAINT employer_ratings_rater_id_fkey FOREIGN KEY (rater_id) REFERENCES public.users(id);


--
-- TOC entry 5287 (class 2606 OID 17243)
-- Name: inclusivity_badges inclusivity_badges_awarded_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.inclusivity_badges
    ADD CONSTRAINT inclusivity_badges_awarded_by_fkey FOREIGN KEY (awarded_by) REFERENCES public.users(id);


--
-- TOC entry 5288 (class 2606 OID 17238)
-- Name: inclusivity_badges inclusivity_badges_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.inclusivity_badges
    ADD CONSTRAINT inclusivity_badges_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5298 (class 2606 OID 17450)
-- Name: interview_schedules interview_schedules_applicant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.interview_schedules
    ADD CONSTRAINT interview_schedules_applicant_id_fkey FOREIGN KEY (applicant_id) REFERENCES public.users(id);


--
-- TOC entry 5299 (class 2606 OID 17455)
-- Name: interview_schedules interview_schedules_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.interview_schedules
    ADD CONSTRAINT interview_schedules_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5300 (class 2606 OID 17445)
-- Name: interview_schedules interview_schedules_job_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.interview_schedules
    ADD CONSTRAINT interview_schedules_job_id_fkey FOREIGN KEY (job_id) REFERENCES public.job_listings(id);


--
-- TOC entry 5275 (class 2606 OID 17052)
-- Name: job_applications job_applications_applicant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_applications
    ADD CONSTRAINT job_applications_applicant_id_fkey FOREIGN KEY (applicant_id) REFERENCES public.users(id);


--
-- TOC entry 5276 (class 2606 OID 17047)
-- Name: job_applications job_applications_job_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_applications
    ADD CONSTRAINT job_applications_job_id_fkey FOREIGN KEY (job_id) REFERENCES public.job_listings(id);


--
-- TOC entry 5272 (class 2606 OID 17000)
-- Name: job_listings job_listings_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_listings
    ADD CONSTRAINT job_listings_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id);


--
-- TOC entry 5277 (class 2606 OID 17076)
-- Name: job_match_scores job_match_scores_job_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_match_scores
    ADD CONSTRAINT job_match_scores_job_id_fkey FOREIGN KEY (job_id) REFERENCES public.job_listings(id) ON DELETE CASCADE;


--
-- TOC entry 5278 (class 2606 OID 17071)
-- Name: job_match_scores job_match_scores_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_match_scores
    ADD CONSTRAINT job_match_scores_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5273 (class 2606 OID 17014)
-- Name: job_required_skills job_required_skills_job_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_required_skills
    ADD CONSTRAINT job_required_skills_job_id_fkey FOREIGN KEY (job_id) REFERENCES public.job_listings(id) ON DELETE CASCADE;


--
-- TOC entry 5274 (class 2606 OID 17019)
-- Name: job_required_skills job_required_skills_skill_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.job_required_skills
    ADD CONSTRAINT job_required_skills_skill_id_fkey FOREIGN KEY (skill_id) REFERENCES public.skills(id);


--
-- TOC entry 5291 (class 2606 OID 17313)
-- Name: mentor_profiles mentor_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentor_profiles
    ADD CONSTRAINT mentor_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5292 (class 2606 OID 17340)
-- Name: mentorship_requests mentorship_requests_mentee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentorship_requests
    ADD CONSTRAINT mentorship_requests_mentee_id_fkey FOREIGN KEY (mentee_id) REFERENCES public.users(id);


--
-- TOC entry 5293 (class 2606 OID 17335)
-- Name: mentorship_requests mentorship_requests_mentor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.mentorship_requests
    ADD CONSTRAINT mentorship_requests_mentor_id_fkey FOREIGN KEY (mentor_id) REFERENCES public.users(id);


--
-- TOC entry 5296 (class 2606 OID 17418)
-- Name: messages messages_conversation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_conversation_id_fkey FOREIGN KEY (conversation_id) REFERENCES public.conversations(id);


--
-- TOC entry 5297 (class 2606 OID 17423)
-- Name: messages messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id);


--
-- TOC entry 5264 (class 2606 OID 16840)
-- Name: micro_tasks micro_tasks_poster_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.micro_tasks
    ADD CONSTRAINT micro_tasks_poster_id_fkey FOREIGN KEY (poster_id) REFERENCES public.users(id);


--
-- TOC entry 5301 (class 2606 OID 17476)
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5263 (class 2606 OID 16789)
-- Name: portfolio_items portfolio_items_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.portfolio_items
    ADD CONSTRAINT portfolio_items_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5252 (class 2606 OID 16605)
-- Name: refresh_tokens refresh_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5254 (class 2606 OID 16652)
-- Name: seeker_disability_info seeker_disability_info_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_disability_info
    ADD CONSTRAINT seeker_disability_info_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5253 (class 2606 OID 16629)
-- Name: seeker_profiles seeker_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_profiles
    ADD CONSTRAINT seeker_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5257 (class 2606 OID 16720)
-- Name: seeker_skills seeker_skills_skill_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_skills
    ADD CONSTRAINT seeker_skills_skill_id_fkey FOREIGN KEY (skill_id) REFERENCES public.skills(id);


--
-- TOC entry 5258 (class 2606 OID 16715)
-- Name: seeker_skills seeker_skills_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.seeker_skills
    ADD CONSTRAINT seeker_skills_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5260 (class 2606 OID 16768)
-- Name: skill_badges skill_badges_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skill_badges
    ADD CONSTRAINT skill_badges_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id);


--
-- TOC entry 5261 (class 2606 OID 16763)
-- Name: skill_badges skill_badges_skill_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skill_badges
    ADD CONSTRAINT skill_badges_skill_id_fkey FOREIGN KEY (skill_id) REFERENCES public.skills(id);


--
-- TOC entry 5262 (class 2606 OID 16758)
-- Name: skill_badges skill_badges_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.skill_badges
    ADD CONSTRAINT skill_badges_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5265 (class 2606 OID 16867)
-- Name: task_applications task_applications_applicant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_applications
    ADD CONSTRAINT task_applications_applicant_id_fkey FOREIGN KEY (applicant_id) REFERENCES public.users(id);


--
-- TOC entry 5266 (class 2606 OID 16862)
-- Name: task_applications task_applications_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_applications
    ADD CONSTRAINT task_applications_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.micro_tasks(id);


--
-- TOC entry 5267 (class 2606 OID 16887)
-- Name: task_completions task_completions_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_completions
    ADD CONSTRAINT task_completions_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.micro_tasks(id);


--
-- TOC entry 5268 (class 2606 OID 16892)
-- Name: task_completions task_completions_worker_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_completions
    ADD CONSTRAINT task_completions_worker_id_fkey FOREIGN KEY (worker_id) REFERENCES public.users(id);


--
-- TOC entry 5269 (class 2606 OID 16925)
-- Name: task_ratings task_ratings_ratee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_ratings
    ADD CONSTRAINT task_ratings_ratee_id_fkey FOREIGN KEY (ratee_id) REFERENCES public.users(id);


--
-- TOC entry 5270 (class 2606 OID 16920)
-- Name: task_ratings task_ratings_rater_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_ratings
    ADD CONSTRAINT task_ratings_rater_id_fkey FOREIGN KEY (rater_id) REFERENCES public.users(id);


--
-- TOC entry 5271 (class 2606 OID 16915)
-- Name: task_ratings task_ratings_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.task_ratings
    ADD CONSTRAINT task_ratings_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.micro_tasks(id);


--
-- TOC entry 5286 (class 2606 OID 17219)
-- Name: workplace_reality_scores workplace_reality_scores_employer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: abilitybridge
--

ALTER TABLE ONLY public.workplace_reality_scores
    ADD CONSTRAINT workplace_reality_scores_employer_id_fkey FOREIGN KEY (employer_id) REFERENCES public.users(id) ON DELETE CASCADE;


-- Completed on 2026-04-30 23:36:26

--
-- PostgreSQL database dump complete
--

\unrestrict LseBOVWRmSPf2QB7cR4fZvkbFplxuw4afHFFq9Io06F0H39qEcAC39DDLLjVNqe

