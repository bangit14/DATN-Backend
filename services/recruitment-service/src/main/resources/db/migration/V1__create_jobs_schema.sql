-- 1. Table: job_categories
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS job_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    parent_id UUID REFERENCES job_categories(id)
);

-- 2. Table: job_posts
CREATE TABLE IF NOT EXISTS job_posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    position VARCHAR(100),
    description TEXT,
    requirements TEXT,
    benefits TEXT,
    job_type VARCHAR(50),
    level VARCHAR(50),
    work_mode VARCHAR(50),
    duration VARCHAR(100),
    location VARCHAR(255),
    min_salary DECIMAL(15,2),
    max_salary DECIMAL(15,2),
    salary_negotiable BOOLEAN NOT NULL DEFAULT FALSE,
    currency VARCHAR(20) NOT NULL DEFAULT 'VND',
    is_remote BOOLEAN NOT NULL DEFAULT FALSE,
    vacancies INT NOT NULL DEFAULT 1,
    deadline DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    view_count INT NOT NULL DEFAULT 0,
    application_count INT NOT NULL DEFAULT 0,
    published_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expired_at TIMESTAMPTZ,
    company_id UUID,
    posted_by UUID,
    nlp_status VARCHAR(50) DEFAULT 'PENDING',
    nlp_error TEXT,
    processed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_job_posts_status ON job_posts(status);
CREATE INDEX IF NOT EXISTS idx_job_posts_company_id ON job_posts(company_id);
CREATE INDEX IF NOT EXISTS idx_job_posts_posted_by ON job_posts(posted_by);
CREATE INDEX IF NOT EXISTS idx_job_posts_slug ON job_posts(slug);
CREATE INDEX IF NOT EXISTS idx_job_posts_deadline ON job_posts(deadline);

-- 3. Table: job_post_norms
CREATE TABLE IF NOT EXISTS job_post_norms (
    job_id UUID PRIMARY KEY REFERENCES job_posts(id) ON DELETE CASCADE,
    skills_norm TEXT[],
    experience_years_min DECIMAL(5,2),
    experience_years_max DECIMAL(5,2),
    experience_level VARCHAR(50),
    education_levels TEXT[],
    majors TEXT[],
    domains TEXT[],
    work_modes_norm TEXT[],
    locations_norm TEXT[],
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    duration_norm_months DECIMAL(5,2),
    model_version VARCHAR(50),
    processed_at TIMESTAMPTZ
);

-- 4. Table: skill_categories
CREATE TABLE IF NOT EXISTS skill_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. Table: skills
CREATE TABLE IF NOT EXISTS skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) UNIQUE NOT NULL,
    category_id UUID NOT NULL REFERENCES skill_categories(id) ON DELETE RESTRICT,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_skills_category_id ON skills(category_id);
CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);

-- 6. Table: job_skills
CREATE TABLE IF NOT EXISTS job_skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    importance_level VARCHAR(50) NOT NULL DEFAULT 'REQUIRED',
    note VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_job_skills_job_skill UNIQUE (job_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_job_skills_job_id ON job_skills(job_id);
CREATE INDEX IF NOT EXISTS idx_job_skills_skill_id ON job_skills(skill_id);

-- 7. Table: saved_jobs
CREATE TABLE IF NOT EXISTS saved_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    job_id UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_saved_jobs_candidate_job UNIQUE (candidate_id, job_id)
);

CREATE INDEX IF NOT EXISTS idx_saved_jobs_candidate_id ON saved_jobs(candidate_id);

-- 8. Table: job_views
CREATE TABLE IF NOT EXISTS job_views (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    candidate_id UUID,
    viewed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_job_views_job_id ON job_views(job_id);

-- 9. Table: industries
CREATE TABLE IF NOT EXISTS industries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200) NOT NULL UNIQUE,
    slug            VARCHAR(200) UNIQUE
);

-- 10. Table: job_industries
CREATE TABLE IF NOT EXISTS job_industries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id          UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    industry_id     UUID NOT NULL REFERENCES industries(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_job_industries UNIQUE (job_id, industry_id)
);

CREATE INDEX IF NOT EXISTS idx_job_industries_job_id ON job_industries(job_id);
CREATE INDEX IF NOT EXISTS idx_job_industries_industry_id ON job_industries(industry_id);
