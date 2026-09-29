-- 1. Enable UUID generator extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- 2. SKILL CATEGORIES & SKILLS MASTER DATA
-- ============================================================
CREATE TABLE IF NOT EXISTS skill_categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS skills (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id     UUID REFERENCES skill_categories(id) ON DELETE SET NULL,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_skills_category_id ON skills(category_id);
CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);

-- ============================================================
-- 3. COMPANIES & COMPANY IMAGES
-- ============================================================
CREATE TABLE IF NOT EXISTS companies (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                        VARCHAR(200) NOT NULL,
    slug                        VARCHAR(200) UNIQUE,
    industry                    VARCHAR(150),
    description                 TEXT,
    benefits                    TEXT,
    logo_url                    VARCHAR(500),
    cover_image_url             VARCHAR(500),
    website_url                 VARCHAR(300),
    address                     VARCHAR(255),
    company_size                VARCHAR(100),
    founded_year                INT,
    verification_status         VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    business_license_file_id    UUID,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS company_images (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id      UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    image_url       VARCHAR(500) NOT NULL,
    caption         VARCHAR(255),
    display_order   INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_company_images_company_id ON company_images(company_id);

-- ============================================================
-- 4. EMPLOYERS (EMPLOYER PROFILES)
-- ============================================================
CREATE TABLE IF NOT EXISTS employers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE,
    company_id      UUID REFERENCES companies(id) ON UPDATE CASCADE ON DELETE SET NULL,
    name            VARCHAR(150) NOT NULL,
    phone           VARCHAR(50),
    avatar_url      VARCHAR(500),
    gender          VARCHAR(20) DEFAULT 'UNKNOWN',
    position        VARCHAR(100),
    is_admin        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_employers_user_id ON employers(user_id);
CREATE INDEX IF NOT EXISTS idx_employers_company_id ON employers(company_id);

-- ============================================================
-- 5. CANDIDATE PROFILES
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_profiles (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL UNIQUE,
    full_name               VARCHAR(150) NOT NULL,
    avatar_url              VARCHAR(500),
    dob                     DATE,
    gender                  VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'UNKNOWN')),
    phone                   VARCHAR(50),
    address                 VARCHAR(255),
    headline                VARCHAR(255),
    summary                 TEXT,
    bio                     TEXT,
    cv_url                  VARCHAR(500),
    cv_text                 TEXT,
    experience_years        INT,
    current_salary          DECIMAL(15,2),
    expected_salary_min     DECIMAL(15,2),
    expected_salary_max     DECIMAL(15,2),
    is_open_to_work         BOOLEAN NOT NULL DEFAULT TRUE,
    profile_visibility      VARCHAR(50) NOT NULL DEFAULT 'PUBLIC',
    public_profile          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_profiles_user_id ON candidate_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_candidate_profiles_visibility ON candidate_profiles(profile_visibility);

-- ============================================================
-- 6. CANDIDATE EDUCATIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_educations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id    UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    school_name     VARCHAR(200) NOT NULL,
    major           VARCHAR(150),
    degree          VARCHAR(50),
    gpa             FLOAT,
    start_date      DATE,
    end_date        DATE,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_educations_candidate_id ON candidate_educations(candidate_id);

-- ============================================================
-- 7. CANDIDATE EXPERIENCES
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_experiences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id    UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    company_name    VARCHAR(200) NOT NULL,
    position        VARCHAR(150) NOT NULL,
    start_date      DATE,
    end_date        DATE,
    is_current      BOOLEAN NOT NULL DEFAULT FALSE,
    description     TEXT,
    achievement     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_experiences_candidate_id ON candidate_experiences(candidate_id);

-- ============================================================
-- 8. CANDIDATE PROJECTS
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_projects (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id    UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    role            VARCHAR(150),
    start_date      DATE,
    end_date        DATE,
    project_url     VARCHAR(500),
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_projects_candidate_id ON candidate_projects(candidate_id);

-- ============================================================
-- 9. CANDIDATE CERTIFICATIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_certifications (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id            UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    name                    VARCHAR(200) NOT NULL,
    issuing_organization    VARCHAR(200),
    issue_date              DATE,
    expiration_date         DATE,
    credential_id           VARCHAR(255),
    credential_url          VARCHAR(500),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_certifications_candidate_id ON candidate_certifications(candidate_id);

-- ============================================================
-- 10. CANDIDATE SKILLS (M-N)
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_skills (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id        UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    skill_id            UUID NOT NULL REFERENCES skills(id) ON UPDATE CASCADE ON DELETE CASCADE,
    level               VARCHAR(50) DEFAULT 'BEGINNER',
    years_of_experience INT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_candidate_skills UNIQUE (candidate_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_candidate_skills_candidate_id ON candidate_skills(candidate_id);
CREATE INDEX IF NOT EXISTS idx_candidate_skills_skill_id ON candidate_skills(skill_id);

-- ============================================================
-- 11. CANDIDATE SOCIAL LINKS
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_social_links (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id    UUID NOT NULL REFERENCES candidate_profiles(id) ON UPDATE CASCADE ON DELETE CASCADE,
    platform        VARCHAR(50) NOT NULL,
    url             VARCHAR(500) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_candidate_social_links_candidate_id ON candidate_social_links(candidate_id);
