CREATE TABLE IF NOT EXISTS cv_norm_snapshot (
    cv_id BIGINT PRIMARY KEY,
    student_id UUID NOT NULL,
    skills_norm JSONB NOT NULL DEFAULT '[]'::jsonb,
    experience_areas JSONB NOT NULL DEFAULT '[]'::jsonb,
    experience_titles JSONB NOT NULL DEFAULT '[]'::jsonb,
    education_level VARCHAR(100),
    education_majors JSONB NOT NULL DEFAULT '[]'::jsonb,
    years_total NUMERIC(10,2),
    model_version VARCHAR(50),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS job_norm_snapshot (
    internship_post_id UUID PRIMARY KEY,
    company_id UUID,
    skills_norm JSONB NOT NULL DEFAULT '[]'::jsonb,
    experience_areas JSONB NOT NULL DEFAULT '[]'::jsonb,
    experience_titles JSONB NOT NULL DEFAULT '[]'::jsonb,
    education_level VARCHAR(100),
    education_majors JSONB NOT NULL DEFAULT '[]'::jsonb,
    min_years NUMERIC(10,2),
    work_mode VARCHAR(50),
    location_lat DOUBLE PRECISION,
    location_lon DOUBLE PRECISION,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cv_norm_snapshot_student_id ON cv_norm_snapshot(student_id);
CREATE INDEX IF NOT EXISTS idx_job_norm_snapshot_company_id ON job_norm_snapshot(company_id);
